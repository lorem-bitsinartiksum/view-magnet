import { Form, Icon, Input, Button, Card, message } from 'antd';
import { Link, Redirect } from 'react-router-dom';
import React from 'react';
import { connect } from 'react-redux';
import axios from 'axios'
import { login } from './../../store/actions';
import 'antd/dist/antd.css';
import './LoginForm.css';

class NormalLoginForm extends React.Component {

    handleSubmit = e => {
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (!err) {
                axios.post('https://bububu.free.beeceptor.com/api/users/login', values, {
                    'Content-Type': 'application/json'
                })
                    .then((res) => {
                        this.props.onLogin(JSON.parse(res.config.data).email, 'TOKEN!');
                        message.success("Logged In!");
                        // localStorage.setItem('login', 'true');
                    })
                    .catch((error) =>
                        console.log(error)
                    );
            }
        })
    };

    render() {
        const { email } = this.props;
        if (email) {
            return <Redirect to='/home' />;
        }
        const { getFieldDecorator } = this.props.form;
        return (
            <Card className="login-card">
                <Form onSubmit={this.handleSubmit} className="login-form">
                    <Form.Item>
                        {getFieldDecorator('email', {
                            rules: [{
                                type: 'email',
                                message: 'The input is not valid Email!',
                            },
                            {
                                required: true,
                                message: 'Please input your Email!',
                            }],
                        })(
                            <Input
                                prefix={<Icon type="mail" style={{ color: 'rgba(0,0,0,.25)' }} />}
                                placeholder="Email"
                            />,
                        )}
                    </Form.Item>
                    <Form.Item>
                        {getFieldDecorator('password', {
                            rules: [{ required: true, message: 'Please input your Password!' }],
                        })(
                            <Input
                                prefix={<Icon type="lock" style={{ color: 'rgba(0,0,0,.25)' }} />}
                                type="password" placeholder="Password"
                            />,
                        )}
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" htmlType="submit" className="login-form-button">Log in</Button>Or <Link to="/register">register now!</Link>
                    </Form.Item>
                </Form>
            </Card>
        );
    }
}

const mapDispatchToProps = dispatch => {
    return {
        onLogin: (email, token) => dispatch(login(email, token)),
    };
};

const LoginForm = Form.create({ name: 'normal_login' })(NormalLoginForm);
export default connect(null, mapDispatchToProps)(LoginForm);
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
                axios.post('http://localhost:7000/api/users/login', { user: values })
                    .then((res) => {
                        if (res.status === 200) {
                            this.props.onLogin(res.data.user.email, res.data.user.token)
                            localStorage.setItem('token', res.data.user.token)
                            message.success("Logged In!")
                        }
                        else console.log(res)
                    })
                    .catch((error) => {
                        message.error(error.response.statusText)
                        this.props.form.resetFields()
                    });
            }
        })
    };

    render() {
        if (this.props.loggedIn) {
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
const mapStateToProps = state => {
    return {
        loggedIn: state.auth.loggedIn,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onLogin: (email, token) => dispatch(login(email, token)),
    };
};

const LoginForm = Form.create({ name: 'normal_login' })(NormalLoginForm);
export default connect(mapStateToProps, mapDispatchToProps)(LoginForm);
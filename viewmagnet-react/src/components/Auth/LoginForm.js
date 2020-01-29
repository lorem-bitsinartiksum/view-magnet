import { Form, Icon, Input, Button, Checkbox, Card, message } from 'antd';
import { Link, Redirect } from 'react-router-dom';
import React from 'react';
import axios from 'axios'
import 'antd/dist/antd.css';
import './LoginForm.css';

class NormalLoginForm extends React.Component {

    state = {
        redirect: false
    }

    handleSubmit = e => {
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (!err) {
                axios.post('https://bububu.free.beeceptor.com', values)
                    .then((res) => {
                        this.setState({ redirect: true });
                        message.success(res.statusText);
                        localStorage.setItem('login', 'true');
                        console.log(res);
                    })
                    .catch((error) =>
                        console.log(error)
                    );
            }
        })
    };

    render() {
        const { redirect } = this.state;
        if (redirect) {
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
                                type="password"
                                placeholder="Password"
                            />,
                        )}
                    </Form.Item>
                    <Form.Item>
                        {getFieldDecorator('remember', {
                            valuePropName: 'checked',
                            initialValue: true,
                        })(<Checkbox className="login-form-checkbox">Remember me</Checkbox>)}
                        <a className="login-form-forgot" href="">Forgot password</a>
                        <Button type="primary" htmlType="submit" className="login-form-button">Log in</Button>Or <Link to="/register">register now!</Link>

                    </Form.Item>
                </Form>
            </Card>
        );
    }
}

const LoginForm = Form.create({ name: 'normal_login' })(NormalLoginForm);
export default LoginForm
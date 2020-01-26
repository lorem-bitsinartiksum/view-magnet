import { Form, Icon, Input, Button, Checkbox, Card } from 'antd';
import React from "react";
import 'antd/dist/antd.css';
import './LoginForm.css';

class NormalLoginForm extends React.Component {
    handleSubmit = e => {
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (!err) {
                console.log('Received values of form: ', values);
            }
        });
    };

    render() {
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
                        <a className="login-form-forgot" href="">
                            Forgot password
          </a>
                        <Button type="primary" htmlType="submit" className="login-form-button">
                            Log in
          </Button>
                        Or <a href="">register now!</a>
                    </Form.Item>
                </Form>
            </Card>
        );
    }
}

const WrappedNormalLoginForm = Form.create({ name: 'normal_login' })(NormalLoginForm);
export default WrappedNormalLoginForm;
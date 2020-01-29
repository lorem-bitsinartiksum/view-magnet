import { Form, Icon, Input, Button, Card, message } from 'antd';
import React from 'react';
import { Link, Redirect } from 'react-router-dom';
import axios from 'axios'
import 'antd/dist/antd.css';
import './RegisterForm.css';

class NormalRegisterForm extends React.Component {
    state = {
        confirmDirty: false,
        redirect: false
    };

    handleSubmit = e => {
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (!err) {
                delete values.confirm;
                axios.post('https://www.jsonstore.io/d39bf48de7b7ef5a2d73ddb0fba06a7523d3b0f1d842c18c5156e4f8d09f7f7d', values).then((res) => {
                    this.setState({ redirect: true });
                    message.success(res.statusText);
                    console.log(res);
                }).catch((error) =>
                    console.log(error)
                )
            }
        });
    };

    handleConfirmBlur = e => {
        const { value } = e.target;
        this.setState({ confirmDirty: this.state.confirmDirty || !!value });
    };

    render() {
        const { redirect } = this.state;
        if (redirect) {
            return <Redirect to='/login' />;
        }
        const { getFieldDecorator } = this.props.form;
        return (
            <Card className="register-card">
                <Form onSubmit={this.handleSubmit} className="register-form">
                    <Form.Item>
                        {getFieldDecorator('username', {
                            rules: [{
                                required: true,
                                message: 'Please enter your company name!',
                            }],
                        })(
                            <Input
                                prefix={<Icon type="home" style={{ color: 'rgba(0,0,0,.25)' }} />}
                                placeholder="Company Name"
                            />,
                        )}
                    </Form.Item>
                    <Form.Item>
                        {getFieldDecorator('email', {
                            rules: [{
                                type: 'email',
                                message: 'The input is not a valid email!',
                            },
                            {
                                required: true,
                                message: 'Please enter your company email!',
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
                            rules: [
                                { required: true, message: 'Please enter your password!' },
                                { min: 6, message: 'Password must be at least 6 characters!' },
                                { validator: this.validateToNextPassword }
                            ],
                        })(
                            <Input
                                prefix={<Icon type="lock" style={{ color: 'rgba(0,0,0,.25)' }} />}
                                type="password"
                                placeholder="Password"
                            />,
                        )}
                    </Form.Item>
                    <Form.Item>
                        {getFieldDecorator('confirm', {
                            rules: [
                                {
                                    required: true,
                                    message: 'Please confirm your password!',
                                },
                                {
                                    validator: this.compareToFirstPassword,
                                },
                            ],
                        })(<Input
                            prefix={<Icon type="lock" style={{ color: 'rgba(0,0,0,.25)' }} />}
                            type="password"
                            placeholder="Re-type your password" onBlur={this.handleConfirmBlur} />)}
                    </Form.Item>
                    <Form.Item>
                        {getFieldDecorator('phone', {
                            rules: [],
                        })(
                            <Input
                                prefix={<Icon type="phone" style={{ color: 'rgba(0,0,0,.25)' }} />}
                                placeholder="Phone"
                            />,
                        )}
                    </Form.Item>
                    <Form.Item>
                        {getFieldDecorator('location', {
                            rules: [],
                        })(
                            <Input
                                prefix={<Icon type="pushpin" style={{ color: 'rgba(0,0,0,.25)' }} />}
                                placeholder="Address"
                            />,
                        )}
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" htmlType="submit" className="register-form-button">Register</Button>Or <Link to="/login">Log in</Link>
                    </Form.Item>
                </Form>
            </Card>
        );
    }

    compareToFirstPassword = (rule, value, callback) => {
        const { form } = this.props;
        if (value && value !== form.getFieldValue('password')) {
            callback('Two passwords that you enter is inconsistent!');
        } else {
            callback();
        }
    };

    validateToNextPassword = (rule, value, callback) => {
        const { form } = this.props;
        if (value && this.state.confirmDirty) {
            form.validateFields(['confirm'], { force: true });
        }
        callback();
    };
}

const RegisterForm = Form.create({ name: 'normal_register' })(NormalRegisterForm);
export default RegisterForm;
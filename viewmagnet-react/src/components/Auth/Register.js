import { Form, Icon, Input, Button, Card, message } from 'antd';
import React from 'react';
import { connect } from 'react-redux';
import { Link, Redirect } from 'react-router-dom';
import axios from 'axios'
import { login } from '../../store/actions'
import 'antd/dist/antd.css';
import './Register.css';

class NormalRegisterForm extends React.Component {
    state = {
        confirmDirty: false,
        redirect: false
    };

    componentDidMount() {
        if (localStorage.getItem('token'))
            this.props.onLogin(localStorage.getItem('token'), localStorage.getItem('email'))
    }

    handleSubmit = e => {
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (!err) {
                delete values.confirm;
                axios.post('http://localhost:7000/api/users', { user: values }).then((res) => {
                    this.setState({ redirect: true });
                    message.success("Account Created!");
                    this.props.onLogin(res.data.user.token, res.data.user.email)
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
        if (this.props.loggedIn) {
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


const mapStateToProps = state => {
    return {
        loggedIn: state.auth.loggedIn,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onLogin: (token, email) => dispatch(login(token, email, false)),
    };
};


const Register = Form.create({ name: 'normal_register' })(NormalRegisterForm);
export default connect(mapStateToProps, mapDispatchToProps)(Register);
import React, { Fragment } from 'react';
import { Form, Input, Card, Button, message } from 'antd';
import { connect } from 'react-redux';
import { login } from '../../store/actions'
import axios from 'axios'


class AdminAuth extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            key: 'tab1',
            authLogin: null,
            authRegister: null,
        };
    }

    onLogin = () => {
        axios.post('http://localhost:7000/api/users/login', { admin: this.state.authLogin })
            .then((res) => {
                message.success("Logged In as Admin!")
                this.props.onLogin(res.data.admin.token, res.data.admin.email)
            }).catch((error) => {
                console.log(error)
                message.error(error.response.statusText)
            })
    }

    onRegister = () => {
        axios.post('http://localhost:7000/api/admins', { admin: this.state.authRegister })
            .then((res) => {
                message.success("Admin Account Created!");
                this.props.onLogin(res.data.admin.token, res.data.admin.email)
            }).catch((error) => {
                console.log(error)
                message.error(error.response.statusText)
            })
    }

    onTabChange = (key, type) => { this.setState({ [type]: key, authLogin: null, authRegister: null }); };

    tabList = [
        { key: 'tab1', tab: 'Login', },
        { key: 'tab2', tab: 'Register', },
    ];

    contentList = {
        tab1:
            <Form layout="inline">
                <Form.Item key="logEma" label="Email"> <Input onChange={(o) => this.setState({ authLogin: { ...this.state.authLogin, email: o.target.value } })} /> </Form.Item>
                <Form.Item key="logPas" label="Password"> <Input.Password onChange={(o) => this.setState({ authLogin: { ...this.state.authLogin, password: o.target.value } })} /> </Form.Item>
                <Button type="primary" onClick={this.onLogin}>Login</Button>
            </Form>,
        tab2:
            <Form labelCol={{ span: 6 }} wrapperCol={{ span: 14 }}>
                <Form.Item key="RegEma" label="Email"> <Input onChange={(o) => this.setState({ authRegister: { ...this.state.authRegister, email: o.target.value } })} /> </Form.Item>
                <Form.Item key="RegUse" label="Username"> <Input onChange={(o) => this.setState({ authRegister: { ...this.state.authRegister, username: o.target.value } })} /> </Form.Item>
                <Form.Item key="RegPas" label="Password"> <Input.Password onChange={(o) => this.setState({ authRegister: { ...this.state.authRegister, password: o.target.value } })} /> </Form.Item>
                <Form.Item key="RegPho" label="Phone"> <Input onChange={(o) => this.setState({ authRegister: { ...this.state.authRegister, phone: o.target.value } })} /> </Form.Item>
                <Button type="primary" onClick={this.onRegister}>Register</Button>
            </Form>,
    };
    render() {
        return (
            <Fragment>
                <Card
                    style={{ margin: 'auto', width: '60%' }}
                    title="Admin Auth" tabList={this.tabList}
                    activeTabKey={this.state.key}
                    onTabChange={key => { this.onTabChange(key, 'key'); }}
                >
                    {this.contentList[this.state.key]}
                </Card>
            </Fragment>
        );
    }
}

const mapStateToProps = state => {
    return {
        // loggedIn: state.auth.loggedIn,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onLogin: (token, email) => dispatch(login(token, email)),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(AdminAuth);
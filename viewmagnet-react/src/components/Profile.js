import './Profile.css';
import 'antd/dist/antd.css';
import React, { Fragment } from 'react';
import { Redirect } from 'react-router-dom';
import axios from 'axios'
import { connect } from 'react-redux';
import { Form, Input, Button, Card, message, Modal, Typography, Popconfirm } from 'antd';
import ButtonGroup from 'antd/lib/button/button-group';
const { Paragraph } = Typography;

class Profile extends React.Component {
    state = {
        username: '',
        email: '',
        phone: '',
        location: '',
        modalVisible: false,
        newPass: '',
        matchingPasswords: false
    };

    componentDidMount() {
        axios.get('http://localhost:7000/api/user', { headers: { 'Authorization': this.props.token } })
            .then((res) => {
                this.setState({
                    username: res.data.user.username,
                    email: res.data.user.email,
                    phone: res.data.user.phone,
                    location: res.data.user.location
                })
            })
            .catch((err) => console.log(err))
    }

    onChangeUsername = str => this.setState({ username: str });
    onChangePhone = str => this.setState({ phone: str });
    onChangeLocation = str => this.setState({ location: str });

    confirm = e => axios.delete('http://localhost:7000/api/user', { headers: { 'Authorization': this.props.token } }).then((res) => message.success(res.data.statusText));
    cancel = e => console.log(e);

    onChangeNewPass = str => this.setState({ newPass: str.target.value });
    onChangeNewPassConfirm = str => this.setState({ matchingPasswords: (this.state.newPass === str.target.value) ? true : false });

    render() {
        if (!this.props.loggedIn) {
            return <Redirect to='/login' />;
        }
        return (<Fragment>
            <Modal visible={this.state.modalVisible} destroyOnClose={true} closable={false} onCancel={() => this.setState({ modalVisible: !this.state.modalVisible })} onOk={() => {
                this.state.matchingPasswords
                    ? (axios.put(
                        'http://localhost:7000/api/user',
                        { user: { password: this.state.newPass, email: this.state.email } },
                        { headers: { 'Authorization': this.props.token } })
                        .then(() => {
                            message.success('Password is updated!');
                            this.setState({ modalVisible: !this.state.modalVisible, newPass: '' });
                        }))
                    : message.error("Passwords do not match!")
            }} okText="Change Password" okType="danger">
                <Card title="Change Password">
                    <Form>
                        <Form.Item label="New Password"><Input type="password" onChange={this.onChangeNewPass} /></Form.Item>
                        <Form.Item label="Confirm New Password"><Input type="password" onChange={this.onChangeNewPassConfirm} /></Form.Item>
                    </Form>
                </Card>
            </Modal>
            <div className="profile-card">
                <Card title="Update Profile">
                    <Paragraph editable={{ onChange: this.onChangeUsername }}>{this.state.username}</Paragraph>
                    <Paragraph editable={{ onChange: this.onChangePhone }}>{this.state.phone}</Paragraph>
                    <Paragraph editable={{ onChange: this.onChangeLocation }}>{this.state.location}</Paragraph>
                    <Button type="primary" onClick={() => {
                        axios.put('http://localhost:7000/api/user', {
                            user: {
                                username: this.state.username,
                                email: this.state.email,
                                phone: this.state.phone,
                                location: this.state.location
                            }
                        }, { headers: { 'Authorization': this.props.token } }).then(() => message.success('User info is updated!'))
                    }}
                    >Save Changes</Button>
                </Card>
                <Card>
                    <ButtonGroup>
                        <Button key="1" type="dashed" style={{ backgroundColor: "white", color: "red" }} onClick={() => this.setState({ modalVisible: !this.state.modalVisible })}>Change Password</Button>
                        <Popconfirm
                            title="Are you sure to delete this account?"
                            onConfirm={this.confirm}
                            onCancel={this.cancel}
                            okText="Delete"
                            cancelText="Cancel"
                            okType="danger"
                        >
                            <Button key="2" type="danger" icon="warning">Delete Account</Button>
                        </Popconfirm>
                    </ButtonGroup>
                </Card>
            </div >
        </Fragment>);
    }
}

const mapStateToProps = state => {
    return {
        loggedIn: state.auth.loggedIn,
        token: state.auth.token,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        // onProfileUpdate: () => dispatch(logout())
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(Profile);
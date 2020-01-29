import './Profile.css';
import 'antd/dist/antd.css';
import React from 'react';
import { Link, Redirect } from 'react-router-dom';
import axios from 'axios'
import { Form, Icon, Input, Button, Card, message, Modal, Radio, Typography, Popconfirm } from 'antd';
import ButtonGroup from 'antd/lib/button/button-group';
const { Paragraph } = Typography;

class Profile extends React.Component {
    state = {
        username: 'username',
        password: 'password',
        phone: 'phone',
        location: 'location',
        modalVisible: false
    };

    onChangeUsername = str => {
        this.setState({ username: str });
    };

    onChangePhone = str => {
        this.setState({ phone: str });
    };

    onChangeLocation = str => {
        this.setState({ location: str });
    };

    confirm = e => {
        console.log(e);
        message.success('Click on Yes');
    }

    cancel = e => {
        console.log(e);
        message.error('Click on No');
    }

    render() {
        return ([
            <Modal visible={this.state.modalVisible} onCancel={() => this.setState({ modalVisible: !this.state.modalVisible })} okText="Change Password" okType="danger">
                <Card title="Change Password">
                    <Form>
                        <Form.Item label="New Password"><Input type="password" /></Form.Item>
                        <Form.Item label="Confirm New Password"><Input type="password" /></Form.Item>
                    </Form>
                </Card>
            </Modal>,
            <div className="profile-card">
                <Card title="Update Profile">
                    <Paragraph editable={{ onChange: this.onChangeUsername }}>{this.state.username}</Paragraph>
                    <Paragraph editable={{ onChange: this.onChangePhone }}>{this.state.phone}</Paragraph>
                    <Paragraph editable={{ onChange: this.onChangeLocation }}>{this.state.location}</Paragraph>
                    <Button type="primary" onClick={() => this.setState({ modalVisible: !this.state.modalVisible })}>Save Changes</Button>
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
            </div>
        ]);
    }
}

export default Profile;
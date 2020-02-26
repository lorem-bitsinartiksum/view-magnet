import React, { Fragment } from 'react'
import { connect } from 'react-redux'
import './Advert.css'
import axios from 'axios'
import { Card, Icon, Upload, message, Col, Typography, Modal, Form, Select, Input, Button, Checkbox } from 'antd';
const { Paragraph } = Typography;
const InputGroup = Input.Group;
const { TextArea } = Input;
const { Option } = Select;
const { Meta } = Card;

const ageOptions = [
    { label: 'Baby', value: '0' },
    { label: 'Child', value: '1' },
    { label: 'Young', value: '2' },
    { label: 'Adult', value: '3' },
    { label: 'Elderly', value: '4' },
];
const ageMapping = [
    { label: 'BABY', value: '0' },
    { label: 'CHILD', value: '1' },
    { label: 'YOUNG', value: '2' },
    { label: 'ADULT', value: '3' },
    { label: 'ELDERLY', value: '4' },
]
const genderOptions = [
    { label: 'Male', value: '0' },
    { label: 'Female', value: '1' },
    { label: 'Undetected', value: '2' }
];
const genderMapping = [
    { label: 'MALE', value: '0' },
    { label: 'FEMALE', value: '1' },
    { label: 'UNDETECTED', value: '2' }
];

class Advert extends React.Component {
    state = {
        modalVisible: false,
    }
    onAgeMapping = (arr) => { return (arr.map(obj => ageMapping.find(o => o.label === obj).value)) }
    onGenderMapping = (arr) => { return (arr.map(obj => genderMapping.find(o => o.label === obj).value)) }
    onClickEdit = () => this.setState({ modalVisible: true })
    onClickDelete = () => axios.delete(
        'http://localhost:7000/api/ads/' + this.props.slug,
        { headers: { 'Authorization': localStorage.getItem('token') } }).then(() => message.success("Advert deleted successfully!")).catch((err) => console.log(err))

    render() {
        return (<Fragment>
            <Modal visible={this.state.modalVisible} destroyOnClose={true} closable={false} onCancel={() => this.setState({ modalVisible: !this.state.modalVisible })} onOk={() => {
                this.state.matchingPasswords
                    ? (axios.update(
                        'http://localhost:7000/api/user',
                        { user: { password: this.state.newPass, email: this.state.email } },
                        { headers: { 'Authorization': localStorage.getItem('token') } })
                        .then(() => {
                            message.success('Password is updated!');
                            this.setState({ modalVisible: !this.state.modalVisible, password: this.state.newPass, newPass: '' });
                        }))
                    : message.error("Passwords do not match!")
            }} okText="Change Password" okType="danger">
                <Form>
                    <Form.Item label="Advert Title">
                        <Input value={this.props.title} onChange={this.onChangeTitle} />
                    </Form.Item>
                    <Form.Item label="Target Age Range">
                        <Checkbox.Group value={this.onAgeMapping(this.props.targetAge)} options={ageOptions} onChange={this.onChangeAgeRange} />
                    </Form.Item>
                    <Form.Item label="Target Gender">
                        <Checkbox.Group value={this.onGenderMapping(this.props.targetGender)} options={genderOptions} onChange={this.onChangeGender} />
                    </Form.Item>
                    <Form.Item label="Target Weather" >
                        <Select value={this.props.targetWeather}
                            filterOption={this.weatherFilterOption} placeholder="Please select target weather" mode="multiple" onChange={this.onChangeWeather}>
                            <Option value="0">Sunny</Option>
                            <Option value="1">Cloudy</Option>
                            <Option value="2">Windy</Option>
                            <Option value="3">Foggy</Option>
                            <Option value="4">Stormy</Option>
                            <Option value="5">Snowy</Option>
                            <Option value="6">Rainy</Option>
                            <Option value="7">Unknown</Option>
                        </Select>
                    </Form.Item>
                    <Form.Item label="Description">
                        <TextArea rows={2} allowClear onChange={this.onChangeDesc} value={this.props.description} />
                    </Form.Item>
                    <Form.Item label="Target Temp Range">
                        <InputGroup compact onChange={f => console.log(f)}>
                            <Input style={{ width: 100, textAlign: 'center' }} value={this.props.targetLowTemp} type="number" placeholder="Minimum" onChange={val => this.setState({ targetLowTemp: val.target.value })} />
                            <Input style={{ width: 30, borderLeft: 0, pointerEvents: 'none', backgroundColor: '#fff', }} placeholder="-" disabled />
                            <Input style={{ width: 100, textAlign: 'center', borderLeft: 0 }} value={this.props.targetHighTemp} type="number" placeholder="Maximum" onChange={val => this.setState({ targetHighTemp: val.target.value })} />
                        </InputGroup>
                    </Form.Item>
                    <Form.Item label="Target Sound Level Range">
                        <InputGroup compact onChange={f => console.log(f)}>
                            <Input style={{ width: 100, textAlign: 'center' }} value={this.props.targetLowSoundLevel} type="number" placeholder="Minimum" onChange={val => this.setState({ targetLowSoundLevel: val.target.value })} />
                            <Input style={{ width: 30, borderLeft: 0, pointerEvents: 'none', backgroundColor: '#fff', }} placeholder="-" disabled />
                            <Input style={{ width: 100, textAlign: 'center', borderLeft: 0 }} value={this.props.targetHighSoundLevel} type="number" placeholder="Maximum" onChange={val => this.setState({ targetHighSoundLevel: val.target.value })} />
                        </InputGroup>
                    </Form.Item>
                </Form>
            </Modal>
            <Col span={8}>
                <Card
                    className="advert-card"
                    cover={<img src={this.props.content} />}
                    actions={[
                        <Icon type="edit" key="edit"
                            onClick={this.onClickEdit} />,
                        <Icon
                            type="delete"
                            key="delete"
                            onClick={this.onClickDelete} />,
                    ]} >
                    <Meta title={this.props.title} description={this.props.description} />
                    <br />
                    targetAge: {this.props.targetAge.toString()}
                    <br />
                    targetGender: {this.props.targetGender.toString()}
                    <br />
                    targetWeather: {this.props.targetWeather.toString()}
                    <br />
                    targetTempRange: [{this.props.targetLowTemp} - {this.props.targetHighTemp}]
                    <br />
                    targetSoundLevelRange: [{this.props.targetLowSoundLevel} - {this.props.targetHighSoundLevel}]
                </Card>
            </Col>
        </Fragment>
        )
    }
}
const mapStateToProps = state => {
    return {
        // token: state.auth.token,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        // onProfileUpdate: () => dispatch(logout())
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(Advert);
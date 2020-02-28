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
    { label: 'BABY', value: '0' },
    { label: 'CHILD', value: '1' },
    { label: 'YOUNG', value: '2' },
    { label: 'ADULT', value: '3' },
    { label: 'ELDERLY', value: '4' },
];
const genderOptions = [
    { label: 'MALE', value: '0' },
    { label: 'FEMALE', value: '1' },
    { label: 'UNDETECTED', value: '2' }
];
const weatherOptions = [
    { label: 'SUNNY', value: '0' },
    { label: 'CLOUDY', value: '1' },
    { label: 'WINDY', value: '2' },
    { label: 'FOGGY', value: '3' },
    { label: 'STORMY', value: '4' },
    { label: 'SNOWY', value: '5' },
    { label: 'RAINY', value: '6' },
    { label: 'UNKNOWN', value: '7' }
];

class Advert extends React.Component {
    state = {
        modalVisible: false,
        title: '',
        slug: '',
        description: '',
        targetAge: [],
        targetGender: [],
        targetWeather: [],
        content: '',
        targetLowTemp: '',
        targetHighTemp: '',
        targetLowSoundLevel: '',
        targetHighSoundLevel: '',
    }

    componentDidMount() {
        this.setState({
            title: this.props.title,
            description: this.props.description,
            targetAge: this.props.targetAge.map(obj => ageOptions.find(o => o.label === obj).value),
            targetGender: this.props.targetGender.map(obj => genderOptions.find(o => o.label === obj).value),
            targetWeather: this.props.targetWeather.map(obj => weatherOptions.find(o => o.label === obj).value),
            content: this.props.content,
            targetLowTemp: this.props.targetLowTemp,
            targetHighTemp: this.props.targetHighTemp,
            targetLowSoundLevel: this.props.targetLowSoundLevel,
            targetHighSoundLevel: this.props.targetHighSoundLevel,
        });
    }

    onClickEdit = () => this.setState({ modalVisible: true })
    onClickDelete = () => axios.delete(
        'http://localhost:7000/api/ads/' + this.props.slug,
        { headers: { 'Authorization': this.props.token } }).then(() => message.success("Advert deleted successfully!")).catch((err) => console.log(err))
    onChangeTitle = str => this.setState({ title: str.target.value });
    onChangeDesc = str => this.setState({ description: str.target.value });
    onChangeAgeRange = rangeSet => this.setState({ targetAge: rangeSet });
    onChangeGender = genderSet => this.setState({ targetGender: genderSet });
    onChangeWeather = weatherSet => this.setState({ targetWeather: weatherSet });
    weatherFilterOption = (input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;

    render() {
        const weatherOptionsSelect = [];
        weatherOptions.map(o => weatherOptionsSelect.push(<Option key={o.value} value={o.value}>{o.label}</Option>));

        return (<Fragment>
            <Modal visible={this.state.modalVisible} destroyOnClose={true} closable={false} onCancel={() => this.setState({ modalVisible: !this.state.modalVisible })} onOk={() => {
                (axios.update('http://localhost:7000/api/user', { user: { password: this.state.newPass, email: this.state.email } }, { headers: { 'Authorization': this.props.token } })
                    .then(() => {
                        message.success('Password is updated!');
                        this.setState({ modalVisible: !this.state.modalVisible, password: this.state.newPass, newPass: '' });
                    }))
            }} okText="Update Advert" okType="primary">
                <Form>
                    <Form.Item label="Advert Title">
                        <Input value={this.state.title} onChange={this.onChangeTitle} />
                    </Form.Item>
                    <Form.Item label="Target Age Range">
                        <Checkbox.Group value={this.state.targetAge} options={ageOptions} onChange={this.onChangeAgeRange} />
                    </Form.Item>
                    <Form.Item label="Target Gender">
                        <Checkbox.Group value={this.state.targetGender} options={genderOptions} onChange={this.onChangeGender} />
                    </Form.Item>
                    <Form.Item label="Target Weather" >
                        <Select defaultValue={this.state.targetWeather} filterOption={this.weatherFilterOption} placeholder="Please select target weather" mode="multiple" onChange={this.onChangeWeather}>
                            {weatherOptionsSelect}
                        </Select>
                    </Form.Item>
                    <Form.Item label="Description">
                        <TextArea rows={2} allowClear onChange={this.onChangeDesc} value={this.state.description} />
                    </Form.Item>
                    <Form.Item label="Target Temp Range">
                        <InputGroup compact onChange={f => console.log(f)}>
                            <Input style={{ width: 100, textAlign: 'center' }} value={this.state.targetLowTemp} type="number" placeholder="Minimum" onChange={val => this.setState({ targetLowTemp: val.target.value })} />
                            <Input style={{ width: 30, borderLeft: 0, pointerEvents: 'none', backgroundColor: '#fff', }} placeholder="-" disabled />
                            <Input style={{ width: 100, textAlign: 'center', borderLeft: 0 }} value={this.state.targetHighTemp} type="number" placeholder="Maximum" onChange={val => this.setState({ targetHighTemp: val.target.value })} />
                        </InputGroup>
                    </Form.Item>
                    <Form.Item label="Target Sound Level Range">
                        <InputGroup compact onChange={f => console.log(f)}>
                            <Input style={{ width: 100, textAlign: 'center' }} value={this.state.targetLowSoundLevel} type="number" placeholder="Minimum" onChange={val => this.setState({ targetLowSoundLevel: val.target.value })} />
                            <Input style={{ width: 30, borderLeft: 0, pointerEvents: 'none', backgroundColor: '#fff', }} placeholder="-" disabled />
                            <Input style={{ width: 100, textAlign: 'center', borderLeft: 0 }} value={this.state.targetHighSoundLevel} type="number" placeholder="Maximum" onChange={val => this.setState({ targetHighSoundLevel: val.target.value })} />
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
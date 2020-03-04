import React, { Fragment } from 'react'
import { connect } from 'react-redux'
import './Advert.css'
import { ageOptions, genderOptions, weatherOptions } from './advertOptions'
import axios from 'axios'
import { Card, Icon, message, Col, Modal, Form, Select, Input, Checkbox } from 'antd';
const InputGroup = Input.Group;
const { TextArea } = Input;
const { Option } = Select;
const { Meta } = Card;

class Advert extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            ...this.props,
            token: this.props.token,
            modalVisible: false,
            targetAge: this.props.targetAge.map(obj => ageOptions.find(o => o.label === obj).value),
            targetGender: this.props.targetGender.map(obj => genderOptions.find(o => o.label === obj).value),
            targetWeather: this.props.targetWeather.map(obj => weatherOptions.find(o => o.label === obj).value),
        }
    }

    onClickUpdate = () => axios.put(
        'http://localhost:7000/api/ads/' + this.props.slug,
        {
            ad: {
                title: this.state.title, description: this.state.description,
                targetAge: this.state.targetAge, targetGender: this.state.targetGender,
                targetWeather: this.state.targetWeather, targetLowTemp: this.state.targetLowTemp,
                targetHighTemp: this.state.targetHighTemp, targetLowSoundLevel: this.state.targetLowSoundLevel,
                targetHighSoundLevel: this.state.targetHighSoundLevel
            }
        },
        { headers: { 'Authorization': this.props.token } }).then(() => { message.success("Advert updated successfully!"); this.setState({ modalVisible: !this.state.modalVisible }) }).catch((err) => console.log(err))
    onCancel = () => this.setState({
        modalVisible: !this.state.modalVisible, ...this.props, targetAge: this.props.targetAge.map(obj => ageOptions.find(o => o.label === obj).value),
        targetGender: this.props.targetGender.map(obj => genderOptions.find(o => o.label === obj).value),
        targetWeather: this.props.targetWeather.map(obj => weatherOptions.find(o => o.label === obj).value),
    });
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
            <Modal visible={this.state.modalVisible} destroyOnClose={true} closable={false} onCancel={this.onCancel} onOk={this.onClickUpdate} okText="Update Advert" okType="primary">
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
                        <InputGroup compact>
                            <Input style={{ width: 100, textAlign: 'center' }} value={this.state.targetLowTemp} type="number" placeholder="Minimum" onChange={val => this.setState({ targetLowTemp: val.target.value })} />
                            <Input style={{ width: 30, borderLeft: 0, pointerEvents: 'none', backgroundColor: '#fff', }} placeholder="-" disabled />
                            <Input style={{ width: 100, textAlign: 'center', borderLeft: 0 }} value={this.state.targetHighTemp} type="number" placeholder="Maximum" onChange={val => this.setState({ targetHighTemp: val.target.value })} />
                        </InputGroup>
                    </Form.Item>
                    <Form.Item label="Target Sound Level Range">
                        <InputGroup compact>
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
                        <Icon type="edit" key="edit" onClick={this.onClickEdit} />,
                        <Icon type="delete" key="delete" onClick={this.onClickDelete} />,
                    ]} >
                    <Meta title={this.state.title} description={this.state.description} />
                    <br />
                    targetAge: {this.state.targetAge.map(obj => ageOptions.find(o => o.value === obj).label).toString()}
                    <br />
                    targetGender: {this.state.targetGender.map(obj => genderOptions.find(o => o.value === obj).label).toString()}
                    <br />
                    targetWeather: {this.state.targetWeather.map(obj => weatherOptions.find(o => o.value === obj).label).toString()}
                    <br />
                    targetTempRange: [{this.state.targetLowTemp} - {this.state.targetHighTemp}]
                    <br />
                    targetSoundLevelRange: [{this.state.targetLowSoundLevel} - {this.state.targetHighSoundLevel}]
                </Card>
            </Col>
        </Fragment>
        )
    }
}
const mapStateToProps = state => {
    return {
        token: state.auth.token,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        // onProfileUpdate: () => dispatch(logout())
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(Advert);
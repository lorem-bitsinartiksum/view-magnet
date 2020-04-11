import './CreateAdvert.css';
import { ageOptions, genderOptions, weatherOptions } from './advertOptions'
import React from 'react'
import axios from 'axios'
import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';
import { Form, Input, Card, Checkbox, Select, Upload, Button, Icon, message, Slider, InputNumber, Row, Col } from 'antd'
const { Option } = Select;
const { TextArea } = Input;
const InputGroup = Input.Group;

class CreateAdvert extends React.Component {
    state = {
        title: '',
        category: '',
        price: 0,
        description: '',
        targetAge: [],
        targetGender: [],
        targetWorldView: 2,
        targetWeather: [],
        content: '',
        targetLowTemp: '',
        targetHighTemp: '',
        targetLowSoundLevel: '',
        targetHighSoundLevel: '',
    }

    onChangeTitle = str => this.setState({ title: str.target.value });
    onChangeCategory = str => this.setState({ category: str.target.value });
    onChangePrice = str => this.setState({ price: str });
    onChangeDesc = str => this.setState({ description: str.target.value });
    onChangeWorldView = str => this.setState({ targetWorldView: str });
    onChangeAgeRange = rangeSet => this.setState({ targetAge: rangeSet });
    onChangeGender = genderSet => this.setState({ targetGender: genderSet });
    onChangeWeather = weatherSet => this.setState({ targetWeather: weatherSet });
    weatherFilterOption = (input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;

    render() {
        if (!this.props.loggedIn) {
            return <Redirect to='/login' />;
        }

        const weatherOptionsSelect = [];
        weatherOptions.map(o => weatherOptionsSelect.push(<Option key={o.value} value={o.value}>{o.label}</Option>));

        return (
            <Card className="advert-card2">
                <Form>
                    <Row>
                        <Col span={11}>
                            <Form.Item label="Advert Title">
                                <Input value={this.state.title} placeholder="Please enter title" onChange={this.onChangeTitle} />
                            </Form.Item>
                            <Form.Item label="Advert Category">
                                <Input value={this.state.category} placeholder="Please enter category" onChange={this.onChangeCategory} />
                            </Form.Item>
                            <Form.Item label="Price">
                                <InputNumber value={this.state.price} onChange={this.onChangePrice} />
                            </Form.Item>
                            <Form.Item label="Target Age Range">
                                <Checkbox.Group value={this.state.targetAge} options={ageOptions} onChange={this.onChangeAgeRange} />
                            </Form.Item>
                            <Form.Item label="Target Gender">
                                <Checkbox.Group value={this.state.targetGender} options={genderOptions} onChange={this.onChangeGender} />
                            </Form.Item>
                            <Form.Item label="Target World View">
                                <Slider min={0} value={this.state.targetWorldView} max={4} tooltipVisible tooltipPlacement="bottom" onChange={this.onChangeWorldView} />
                            </Form.Item>
                        </Col>
                        <Col span={2}>
                        </Col>
                        <Col span={11}>
                            <Form.Item label="Target Weather" >
                                <Select value={this.state.targetWeather}
                                    filterOption={this.weatherFilterOption} placeholder="Please select target weather" mode="multiple" onChange={this.onChangeWeather}>
                                    {weatherOptionsSelect}
                                </Select>
                            </Form.Item>
                            <Form.Item label="Upload Advert Poster">
                                <Upload listType="picture" beforeUpload={(f) => { // TODO USE YOUR OWN FILELIST
                                    let reader = new FileReader();
                                    reader.readAsDataURL(f);
                                    reader.onloadend = () => { this.setState({ content: reader.result }); return false; };
                                }}>
                                    <Button>
                                        <Icon type="upload" />Click to upload
                            </Button>
                                </Upload>
                            </Form.Item>
                            <Form.Item label="Description">
                                <TextArea rows={2} allowClear onChange={this.onChangeDesc} value={this.state.description} />
                            </Form.Item>
                            <Form.Item label="Target Temp Range">
                                <InputGroup compact onChange={f => console.log(f)}>
                                    <Input style={{ width: 100, textAlign: 'center' }} value={this.state.targetLowTemp} type="number" placeholder="Minimum" onChange={val => this.setState({ targetLowTemp: val.target.value })} />
                                    <Input style={{ width: 30, borderLeft: 0, pointerEvents: 'none', backgroundColor: '#fff', }} placeholder="-" disabled />
                                    <Input style={{ width: 100, textAlign: 'center', borderLeft: 0 }} value={this.state.targetHighTemp} placeholder="Maximum" onChange={val => this.setState({ targetHighTemp: val.target.value })} />
                                </InputGroup>
                            </Form.Item>
                            <Form.Item label="Target Sound Level Range">
                                <InputGroup compact onChange={f => console.log(f)}>
                                    <Input style={{ width: 100, textAlign: 'center' }} value={this.state.targetLowSoundLevel} type="number" placeholder="Minimum" onChange={val => this.setState({ targetLowSoundLevel: val.target.value })} />
                                    <Input style={{ width: 30, borderLeft: 0, pointerEvents: 'none', backgroundColor: '#fff', }} placeholder="-" disabled />
                                    <Input style={{ width: 100, textAlign: 'center', borderLeft: 0 }} value={this.state.targetHighSoundLevel} placeholder="Maximum" onChange={val => this.setState({ targetHighSoundLevel: val.target.value })} />
                                </InputGroup>
                            </Form.Item>
                            <Form.Item>
                                <Button type="primary" onClick={() => {
                                    this.setState({
                                        title: '',
                                        category: '',
                                        price: 0,
                                        description: '',
                                        targetAge: [],
                                        targetGender: [],
                                        targetWeather: [],
                                        targetWorldView: 2,
                                        targetLowTemp: '',
                                        targetHighTemp: '',
                                        targetLowSoundLevel: '',
                                        targetHighSoundLevel: '',
                                    }); axios.post("http://localhost:7000/api/ads", { ad: this.state }, { headers: { 'Authorization': this.props.token } }).then(() => message.success("Advert created succesfully!")).catch(() => message.warning("Something went wrong!"))
                                }}>Create Advert</Button>
                            </Form.Item>
                        </Col>
                    </Row>
                </Form>
            </Card >
        );
    }
}

const mapStateToProps = state => {
    return {
        token: state.auth.token,
        loggedIn: state.auth.loggedIn,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        // onLogin: (token) => dispatch(login(token)),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(CreateAdvert);
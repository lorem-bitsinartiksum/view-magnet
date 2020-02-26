import React from 'react'
import axios from 'axios'
import './CreateAdvert.css';
import { connect } from 'react-redux';
import { Form, Input, Card, Checkbox, Select, Upload, Button, Icon, message } from 'antd'
const { Option } = Select;
const { TextArea } = Input;
const InputGroup = Input.Group;

const ageOptions = [
    { label: 'Baby', value: '0' },
    { label: 'Child', value: '1' },
    { label: 'Young', value: '2' },
    { label: 'Adult', value: '3' },
    { label: 'Elderly', value: '4' },
];

const genderOptions = [
    { label: 'Male', value: '0' },
    { label: 'Female', value: '1' },
    { label: 'Undetected', value: '2' }
];

class CreateAdvert extends React.Component {
    state = {
        title: '',
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

    onChangeTitle = str => this.setState({ title: str.target.value });
    onChangeDesc = str => this.setState({ description: str.target.value });
    onChangeAgeRange = rangeSet => this.setState({ targetAge: rangeSet });
    onChangeGender = genderSet => this.setState({ targetGender: genderSet });
    onChangeWeather = weatherSet => this.setState({ targetWeather: weatherSet });
    weatherFilterOption = (input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;

    render() {
        return (
            <Card className="advert-card">
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
                        <Select value={this.state.targetWeather}
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
                                description: '',
                                targetAge: [],
                                targetGender: [],
                                targetWeather: [],
                                targetLowTemp: '',
                                targetHighTemp: '',
                                targetLowSoundLevel: '',
                                targetHighSoundLevel: '',
                            }); axios.post("http://localhost:7000/api/ads", { ad: this.state }, { headers: { 'Authorization': localStorage.getItem('token') } }).then(() => message.success("Advert created succesfully!")).catch(() => message.warning("Something went wrong!"))
                        }}>Create Advert</Button>
                    </Form.Item>
                </Form>
            </Card >
        );
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

export default connect(mapStateToProps, mapDispatchToProps)(CreateAdvert);
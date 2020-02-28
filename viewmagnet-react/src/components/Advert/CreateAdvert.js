import './CreateAdvert.css';
import React from 'react'
import axios from 'axios'
import { connect } from 'react-redux';
import { login } from './../../store/actions';
import { Redirect } from 'react-router-dom';
import { Form, Input, Card, Checkbox, Select, Upload, Button, Icon, message } from 'antd'
const { Option } = Select;
const { TextArea } = Input;
const InputGroup = Input.Group;

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
        if (!this.props.loggedIn) {
            return <Redirect to='/login' />;
        }

        const weatherOptionsSelect = [];
        weatherOptions.map(o => weatherOptionsSelect.push(<Option key={o.value} value={o.value}>{o.label}</Option>));

        return (
            <Card className="advert-card2">
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
                                description: '',
                                targetAge: [],
                                targetGender: [],
                                targetWeather: [],
                                targetLowTemp: '',
                                targetHighTemp: '',
                                targetLowSoundLevel: '',
                                targetHighSoundLevel: '',
                            }); axios.post("http://localhost:7000/api/ads", { ad: this.state }, { headers: { 'Authorization': this.props.token } }).then(() => message.success("Advert created succesfully!")).catch(() => message.warning("Something went wrong!"))
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
        email: state.auth.email,
        loggedIn: state.auth.loggedIn,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        // onLogin: (token) => dispatch(login(token)),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(CreateAdvert);
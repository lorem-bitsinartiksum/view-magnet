import './CreateAdvert.css'
import React from 'react'
import axios from 'axios'
import { connect } from 'react-redux';
import { Form, Input, Card, Radio, Select, Upload, Button, Icon, message } from 'antd'
const { Option } = Select;
const { TextArea } = Input;

class CreateAdvert extends React.Component {
    state = {
        title: '',
        description: '',
        targetAge: '',
        targetGender: '',
        targetWeather: '',
        content: '',
    }
    onChangeTitle = str => this.setState({ title: str.target.value });
    onChangeDesc = str => this.setState({ description: str.target.value });
    onChangeAgeRange = str => this.setState({ targetAge: [str.target.value] });
    onChangeGender = str => this.setState({ targetGender: [str.target.value] });
    onChangeWeather = str => this.setState({ targetWeather: [str] });

    render() {
        return (
            <Card className="advert-card">
                <Form>
                    <Form.Item label="Advert Title">
                        <Input onChange={this.onChangeTitle} value={this.state.title} />
                    </Form.Item>
                    <Form.Item label="Target Age Range">
                        <Radio.Group onChange={this.onChangeAgeRange} value={this.state.targetAge}>
                            <Radio.Button value="0">Baby</Radio.Button>
                            <Radio.Button value="1">Child</Radio.Button>
                            <Radio.Button value="2">Young</Radio.Button>
                            <Radio.Button value="3">Adult</Radio.Button>
                            <Radio.Button value="4">Elderly</Radio.Button>
                        </Radio.Group>
                    </Form.Item>
                    <Form.Item label="Target Gender">
                        <Radio.Group onChange={this.onChangeGender} value={this.state.targetGender}>
                            <Radio.Button value="0">Male</Radio.Button>
                            <Radio.Button value="1">Female</Radio.Button>
                            <Radio.Button value="2">Undetected</Radio.Button>
                        </Radio.Group>
                    </Form.Item>
                    <Form.Item label="Target Weather" >
                        <Select placeholder="Please select target weather" onChange={this.onChangeWeather} value={this.state.targetWeather}>
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
                    <Form.Item>
                        <Button onClick={() => {
                            this.setState({
                                title: '',
                                description: '',
                                targetAge: '',
                                targetGender: '',
                                targetWeather: '',
                            }); axios.post("http://localhost:7000/api/ads", { ad: this.state }, { headers: { 'Authorization': localStorage.getItem('token') } }).then(() => message.success("Advert created succesfully!"))
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
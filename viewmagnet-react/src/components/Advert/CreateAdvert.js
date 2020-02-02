import './CreateAdvert.css'
import React from 'react'
import { Form, Input, Card, Radio, Select, Upload, Button, Icon } from 'antd'
const { Option } = Select;
const { TextArea } = Input;

class CreateAdvert extends React.Component {
    state = {
        title: "",
        description: "",
        ageRange: "",
        gender: "",
        weather: "",
        poster: "",
    }
    onChangeTitle = str => this.setState({ title: str.target.value });
    onChangeDesc = str => this.setState({ description: str.target.value });
    onChangeAgeRange = str => this.setState({ ageRange: str.target.value });
    onChangeGender = str => this.setState({ gender: str.target.value });
    onChangeWeather = str => this.setState({ weather: str });

    render() {
        return (
            <Card className="advert-card">
                <Form>
                    <Form.Item label="Advert Title">
                        <Input onChange={this.onChangeTitle} />
                    </Form.Item>
                    <Form.Item label="Target Age Range">
                        <Radio.Group onChange={this.onChangeAgeRange} >
                            <Radio.Button value="BABY">Baby</Radio.Button>
                            <Radio.Button value="CHILD">Child</Radio.Button>
                            <Radio.Button value="YOUNG">Young</Radio.Button>
                            <Radio.Button value="ADULT">Adult</Radio.Button>
                            <Radio.Button value="ELDERLY">Elderly</Radio.Button>
                        </Radio.Group>
                    </Form.Item>
                    <Form.Item label="Target Gender">
                        <Radio.Group onChange={this.onChangeGender}>
                            <Radio.Button value="MALE">Male</Radio.Button>
                            <Radio.Button value="FEMALE">Female</Radio.Button>
                            <Radio.Button value="UNDETECTED">Undetected</Radio.Button>
                        </Radio.Group>
                    </Form.Item>
                    <Form.Item label="Target Weather" >
                        <Select placeholder="Please select target weather" onChange={this.onChangeWeather}>
                            <Option value="SUNNY">Sunny</Option>
                            <Option value="CLOUDY">Cloudy</Option>
                            <Option value="WINDY">Windy</Option>
                            <Option value="FOGGY">Foggy</Option>
                            <Option value="STORMY">Stormy</Option>
                            <Option value="SNOWY">Snowy</Option>
                            <Option value="RAINY">Rainy</Option>
                            <Option value="UNKNOWN">Unknown</Option>
                        </Select>
                    </Form.Item>
                    <Form.Item label="Upload Advert Poster">
                        <Upload listType="picture" beforeUpload={(f) => {
                            let reader = new FileReader();
                            reader.readAsDataURL(f);
                            reader.onloadend = () => { this.setState({ poster: reader.result }); return false; };
                        }}>
                            <Button>
                                <Icon type="upload" />Click to upload
                            </Button>
                        </Upload>
                    </Form.Item>
                    <Form.Item label="Description">
                        <TextArea rows={2} allowClear onChange={this.onChangeDesc} />
                    </Form.Item>
                    <Form.Item>
                        <Button onClick={() => console.log(this.state)}>Create Advert</Button>
                    </Form.Item>
                </Form>
            </Card >
        );
    }
}

export default CreateAdvert;
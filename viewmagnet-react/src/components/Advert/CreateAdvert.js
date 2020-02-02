import './CreateAdvert.css'
import React from 'react'
import { Form, Input, Card, Radio, Select, Upload, Button, Icon } from 'antd'
const { Option } = Select;
const { TextArea } = Input;

class CreateAdvert extends React.Component {
    render() {
        return (
            <Card className="advert-card">
                <Form>
                    <Form.Item label="Advert Title">
                        <Input />
                    </Form.Item>
                    <Form.Item label="Target Age Range">
                        <Radio.Group>
                            <Radio.Button value="BABY">Baby</Radio.Button>
                            <Radio.Button value="CHILD">Child</Radio.Button>
                            <Radio.Button value="YOUNG">Young</Radio.Button>
                            <Radio.Button value="ADULT">Adult</Radio.Button>
                            <Radio.Button value="ELDERLY">Elderly</Radio.Button>
                        </Radio.Group>
                    </Form.Item>
                    <Form.Item label="Target Gender">
                        <Radio.Group>
                            <Radio.Button value="MALE">Male</Radio.Button>
                            <Radio.Button value="FEMALE">Female</Radio.Button>
                            <Radio.Button value="UNDETECTED">Undetected</Radio.Button>
                        </Radio.Group>
                    </Form.Item>
                    <Form.Item label="Target Weather" >
                        <Select placeholder="Please select target weather">
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
                        <Upload name="logo" action="/upload.do" listType="picture">
                            <Button>
                                <Icon type="upload" />Click to upload
                            </Button>
                        </Upload>
                    </Form.Item>
                    <Form.Item label="Description">
                        <TextArea rows={2} allowClear />
                    </Form.Item>
                    <Form.Item>
                        <Button>Create Advert</Button>
                    </Form.Item>
                </Form>
            </Card>
        );
    }
}

export default CreateAdvert;
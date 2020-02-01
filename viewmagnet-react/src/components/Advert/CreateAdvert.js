import './CreateAdvert.css'
import React from 'react'
import { Form, Input, Card } from 'antd'

class CreateAdvert extends React.Component {
    render() {
        return (
            <Card className="advert-card">
                <Form>
                    <Form.Item>
                        <Input />
                    </Form.Item>
                </Form>
            </Card>
        );
    }
}

export default CreateAdvert;
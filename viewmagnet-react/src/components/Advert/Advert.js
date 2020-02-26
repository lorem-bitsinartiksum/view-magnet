import React from 'react'
import { connect } from 'react-redux'
import './Advert.css'
import axios from 'axios'
import { Card, Icon, message, Col } from 'antd';
const { Meta } = Card;

class Advert extends React.Component {
    render() {
        return (
            <Col span={8}>
                <Card
                    className="advert-card"
                    cover={<img src={this.props.content} />}
                    actions={[
                        <Icon type="edit" key="edit" />,
                        <Icon
                            type="delete"
                            key="delete"
                            onClick={() => axios.delete(
                                'http://localhost:7000/api/ads/' + this.props.slug,
                                { headers: { 'Authorization': localStorage.getItem('token') } }).then(() => message.success("Advert deleted successfully!")).catch((err) => console.log(err))} />,
                    ]} >
                    <Meta title={this.props.title} description={this.props.description} />
                    <br />
                    targetAge: {this.props.targetAge}
                    <br />
                    targetGender: {this.props.targetGender}
                    <br />
                    targetWeather: {this.props.targetWeather}
                    <br />
                    targetTempRange: [{this.props.targetLowTemp} - {this.props.targetHighTemp}]
                    <br />
                    targetSoundLevelRange: [{this.props.targetLowSoundLevel} - {this.props.targetHighSoundLevel}]
                </Card>
            </Col>
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
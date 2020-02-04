import React, { Fragment } from 'react'
import './Advert.css'
import axios from 'axios'
import { connect } from 'react-redux'
import { Card, Icon } from 'antd';
const { Meta } = Card;


class Advert extends React.Component {

    state = {
        title: "",
        description: "",
        targetAge: "",
        targetGender: "",
        targetWeather: "",
        // content: "",
    }

    componentDidMount() {
        axios.get('http://localhost:7000/api/ads?email=info@thenorthface.com', { headers: { 'Authorization': localStorage.getItem('token') } })
            .then((res) => {
                console.log(res.data.ads[0]); this.setState({
                    title: res.data.ads[0].title,
                    description: res.data.ads[0].description,
                    targetAge: res.data.ads[0].targetAge,
                    targetGender: res.data.ads[0].targetGender,
                    targetWeather: res.data.ads[0].targetWeather,
                });
            })
            .catch((err) => console.log(err))
    }

    render() {
        return (
            <Fragment>
                <Card
                    className="advert-card"
                    title={this.state.title}
                    cover={
                        <img
                        // src="https://gw.alipayobjects.com/zos/rmsportal/JiqGstEfoWAOHiTxclqi.png"
                        />
                    }
                    actions={[
                        <Icon type="edit" key="edit" />,
                        <Icon type="delete" key="delete" />,
                    ]}
                >
                    <Meta
                        title={this.state.description}
                    />
                    <br />
                    targetAge: {this.state.targetAge}
                    <br />
                    targetGender: {this.state.targetGender}
                    <br />
                    targetWeather: {this.state.targetWeather}
                </Card>
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
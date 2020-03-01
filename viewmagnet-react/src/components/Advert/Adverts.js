import React, { Fragment } from 'react'
import './Advert'
import './Adverts.css'
import { connect } from 'react-redux'
import { Redirect } from 'react-router-dom';
import axios from 'axios'
import Advert from './Advert'
import { Row } from 'antd'

class Adverts extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            ads: null,
        }
    }

    componentDidMount() {
        let url = this.props.isAdmin ? 'http://localhost:7000/api/ads' : 'http://localhost:7000/api/ads?email=' + this.props.email;
        axios.get(url, { headers: { 'Authorization': this.props.token } })
            .then((res) => this.setState({ ads: res.data.ads }))
            .catch((err) => console.log(err))
    }

    render() {
        if (!this.props.loggedIn) {
            return <Redirect to='/login' />;
        }
        let ads = null
        if (this.state.ads)
            ads = this.state.ads.map(ad => (
                <Advert
                    key={ad.slug}
                    slug={ad.slug}
                    title={ad.title}
                    description={ad.description}
                    targetAge={ad.targetAge}
                    targetGender={ad.targetGender}
                    targetWeather={ad.targetWeather}
                    targetLowTemp={ad.targetLowTemp}
                    targetHighTemp={ad.targetHighTemp}
                    targetLowSoundLevel={ad.targetLowSoundLevel}
                    targetHighSoundLevel={ad.targetHighSoundLevel}
                    content={ad.content}
                />
            ))
        return (
            <Fragment>
                <Row>
                    {ads}
                </Row>
            </Fragment >
        )
    }
}

const mapStateToProps = state => {
    return {
        token: state.auth.token,
        email: state.auth.email,
        isAdmin: state.auth.isAdmin,
        loggedIn: state.auth.loggedIn,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        // onLogin: (token) => dispatch(login(token)),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(Adverts);
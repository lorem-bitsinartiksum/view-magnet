import React, { Fragment } from 'react'
import './Advert'
import './Adverts.css'
import { connect } from 'react-redux'
import axios from 'axios'
import Advert from './Advert'
import { Row } from 'antd'

class Adverts extends React.Component {
    state = {
        ads: null
    }

    componentDidMount() {
        axios.get('http://localhost:7000/api/ads?email=info@mi.com', { headers: { 'Authorization': localStorage.getItem('token') } })
            .then((res) => {
                this.setState({ ads: res.data.ads });
            })
            .catch((err) => console.log(err))
    }

    render() {
        let ads = null
        if (this.state.ads)
            ads = this.state.ads.map(ad => (
                <Advert
                    slug={ad.slug}
                    title={ad.title}
                    description={ad.description}
                    targetAge={ad.targetAge}
                    targetGender={ad.targetGender}
                    targetWeather={ad.targetWeather}
                    content={ad.content} />
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
        // token: state.auth.token,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        // onProfileUpdate: () => dispatch(logout())
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(Adverts);
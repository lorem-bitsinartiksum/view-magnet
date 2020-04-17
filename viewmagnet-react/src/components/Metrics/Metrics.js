import React, { Fragment } from 'react';
import { Row, Col } from 'antd';

class Metrics extends React.Component {
    render() {
        return (
            <Fragment>
                <Row>
                    <Col span={24}>
                        <iframe src="http://localhost:3000/d/pMoo_G9Zz/billboard_status?orgId=1&from=1586982649842&to=1587155449842&var-billboard=&var-mode=" width="1600" height="950" frameBorder="0"></iframe>
                    </Col>
                </Row>
                <Row>
                    <Col span={24}>
                        <iframe src="http://localhost:3000/d/I1eAGIrWz/person?orgId=1&from=1586981229959&to=1587154029959&var-ad=&var-mode=" width="1600" height="775" frameBorder="0"></iframe>
                    </Col>
                </Row>
                <Row>
                    <Col span={24}>
                        <iframe src="http://localhost:3000/d/z-RU4S9Wz/ad_pool?orgId=1&from=1587133887306&to=1587155487310&var-billboard=&var-mode=" width="1600" height="500" frameBorder="0"></iframe>
                    </Col>
                </Row>
            </Fragment>
        );
    }
}

export default Metrics;
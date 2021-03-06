import React from 'react';
import './Bars.css';
import { Layout, Menu, Icon, Card } from 'antd';
import { Link } from 'react-router-dom';
import { connect } from 'react-redux';
import { logout } from './../store/actions';
const { Header, Content, Sider } = Layout;

class Bars extends React.Component {
  state = {
    collapsed: true,
  };

  onCollapse = collapsed => {
    this.setState({ collapsed });
  };

  render() {
    let sidebarItems = ([
      <Menu.Item key="1">
        <Link to="/login"><Icon type="login" /><span>Log in</span></Link>
      </Menu.Item>,
      <Menu.Item key="2">
        <Link to="/register"><Icon type="user-add" /><span>Register</span></Link>
      </Menu.Item>,
    ]);
    if (this.props.loggedIn) {
      sidebarItems = ([
        this.props.isAdmin ? null :
          <Menu.Item key="3">
            <Link to="/create-ad">
              <Icon type="file-add" /> <span>Create Advert</span>
            </Link>
          </Menu.Item >,
        <Menu.Item key="4">
          <Link to="/adverts">
            <Icon type="file-image" /> <span>Adverts</span>
          </Link>
        </Menu.Item >,
        this.props.isAdmin ? null :
          <Menu.Item key="5">
            <Link to="/profile">
              <Icon type="user" /> <span>Profile</span>
            </Link>
          </Menu.Item >,
        <Menu.Item key="6">
          <Link to="/home" onClick={() => { this.props.onLogout(); }}>
            <Icon type="logout" /> <span>Log out</span>
          </Link>
        </Menu.Item >
      ]);
    }

    return (
      <Layout style={{ minHeight: '100vh' }}>
        <Header>
          <Link to="/"><h1 className="title">ViewMagnet</h1></Link>
        </Header>
        <Layout>
          <Sider collapsible collapsed={this.state.collapsed} onCollapse={this.onCollapse}>
            <Menu>
              {sidebarItems}
            </Menu>
          </Sider>
          <Content>
            <Card style={{ minHeight: '88.6vh' }}>
              {this.props.children}
            </Card>
          </Content>
        </Layout>
      </Layout>
    );
  }
}

const mapStateToProps = state => {
  return {
    loggedIn: state.auth.loggedIn,
    isAdmin: state.auth.isAdmin,
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onLogout: () => dispatch(logout())
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Bars);
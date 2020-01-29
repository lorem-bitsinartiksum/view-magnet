import React from 'react';
import './Bars.css';
import { Layout, Menu, Icon, Button } from 'antd';
import { Link } from 'react-router-dom';
const { Header, Content, Sider } = Layout;

class Bars extends React.Component {
  state = {
    collapsed: true,
  };

  onCollapse = collapsed => {
    console.log(collapsed);
    this.setState({ collapsed });
  };

  render() {
    let sidebarItems = ([
      <Menu.Item key="1">
        <Link to="/login"><Icon type="login" /><span>Log in</span></Link>
      </Menu.Item>,
      <Menu.Item key="2">
        <Link to="/register"><Icon type="user-add" /><span>Register</span></Link>
      </Menu.Item>
    ]);

    if (localStorage.getItem('login') === 'true') {
      sidebarItems = (
        <Menu.Item key="3">
          <Link to="/home" onClick={() => localStorage.clear()}>
            <Icon type="logout" />
            <span>Log out</span>
          </Link>
        </Menu.Item >
      )
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
            {this.props.children}
          </Content>
        </Layout>
      </Layout>
    );
  }
}

export default Bars; 
import React from 'react';
import './Bars.css';
import { Layout, Menu, Breadcrumb, Icon } from 'antd';
const { Header, Content, Footer, Sider } = Layout;
const { SubMenu } = Menu;

class SiderDemo extends React.Component {
  state = {
    collapsed: false,
  };

  onCollapse = collapsed => {
    console.log(collapsed);
    this.setState({ collapsed });
  };

  render(props) {
    return (
      <Layout style={{ minHeight: '110vh' }}>
        <Header>header</Header>
        <Layout>
          <Sider collapsible collapsed={this.state.collapsed} onCollapse={this.onCollapse}>
            <Menu>
              <Menu.Item key="1">
                <Icon type="login" />
                <span>Log in</span>
              </Menu.Item>
              <Menu.Item key="2">
                <Icon type="user-add" />
                <span>Register</span>
              </Menu.Item>
              <Menu.Item key="3">
                <Icon type="logout" />
                <span>Log out</span>
              </Menu.Item>
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

export default SiderDemo; 
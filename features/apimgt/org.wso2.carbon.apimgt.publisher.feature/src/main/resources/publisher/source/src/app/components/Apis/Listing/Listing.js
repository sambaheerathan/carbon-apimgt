/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import React from 'react'
import qs from 'qs'

import ApiThumb from './ApiThumb'
import '../Apis.css'
import API from '../../../data/api.js'
import Loading from '../../Base/Loading/Loading'
import ResourceNotFound from "../../Base/Errors/ResourceNotFound";
import {Link} from 'react-router-dom'
import {Table, Icon, Menu, Dropdown, Button, Row, Col} from 'antd';

const columns = [{
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
    render: (text, record) => <Link to={"/apis/" + record.id}>{text}</Link>,
}, {
    title: 'Context',
    dataIndex: 'context',
    key: 'context',
}, {

    title: 'Version',
    dataIndex: 'version',
    key: 'version',
}, {
    title: 'Action',
    key: 'action',
    render: (text, record) => <Link to=""><Icon type="delete"/> Delete</Link>,
}];

const menu = (
    <Menu>
        <Menu.Item>
            <Link to="/api/create/swagger">Create new API with Swagger</Link>
        </Menu.Item>
        <Menu.Item>
            <Link to="/api/create/rest">Create new API</Link>
        </Menu.Item>
    </Menu>
);
const ButtonGroup = Button.Group;
class Listing extends React.Component {
    constructor(props) {
        super(props);
        this.state = {listType: 'grid', apis: null};
    }

    componentDidMount() {
        let api = new API();
        let promised_apis = api.getAll();
        promised_apis.then((response) => {
            this.setState({apis: response.obj})
        }).catch(error => {
            if (process.env.NODE_ENV !== "production")
                console.log(error);
            let status = error.status;
            if (status === 404) {
                this.setState({notFound: true});
            } else if (status === 401) {
                this.setState({isAuthorize: false});
                let params = qs.stringify({reference: this.props.location.pathname});
                this.props.history.push({pathname: "/login", search: params});
            }
        });
    }

    setListType = (value) => {
        this.setState({listType: value});
    }

    isActive = (value) => {
        return 'btn ' + ((value === this.state.listType) ? 'active' : 'default');
    }

    render() {
        if (this.state.notFound) {
            return <ResourceNotFound/>
        }
        return (
            <div>
                <div className="api-add-links">
                    <Dropdown overlay={menu} placement="topRight">
                        <Button shape="circle" icon="plus"/>
                    </Dropdown>
                </div>
                <div className="flex-container">
                    <h2>All APIs</h2>
                    <ButtonGroup className="api-type-selector">
                        <Button type="default" icon="bars" onClick={() => this.setListType('list')}/>
                        <Button type="default" icon="appstore" onClick={() => this.setListType('grid')}/>
                    </ButtonGroup>
                </div>
                {
                    this.state.apis ?
                        this.state.listType === "list" ?
                            <Row type="flex" justify="start">
                                <Col span={24}>
                                    <Table columns={columns} dataSource={this.state.apis.list} bordered style={{margin:'10px'}}/>
                                </Col>
                            </Row>
                            : <Row type="flex" justify="start">
                            {this.state.apis.list.map((api, i) => {
                                return <ApiThumb key={api.id} listType={this.state.listType} api={api}/>
                            })}
                        </Row>
                        : <Loading/>
                }
            </div>
        );
    }
}

export default Listing
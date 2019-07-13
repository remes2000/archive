import * as React from 'react';
import { Route, RouteProps } from 'react-router-dom';
import { Layout } from './components/Layout';
import Home from './components/Pages/Home';
import Login from './components/Pages/Login';
import Register from './components/Pages/Register';
import CreateProject from './components/Pages/CreateProject'
import MyProjects from './components/Pages/MyProjects'
import Project from './components/Pages/Project/Project'
import Table from './components/Pages/Table/Table'
import UserRoute from './components/Routes/UserRoute'
import GuestRoute from './components/Routes/GuestRoute'

export const routes =
    <Layout>
        <Route exact path="/" component={Home} />
        <GuestRoute exact path="/login" component={Login} />
        <GuestRoute exact path="/register" component={Register} />
        <UserRoute exact path="/createproject" component={CreateProject} />
        <UserRoute exact path="/myprojects" component={MyProjects} />
        <Route path="/project/:id" component={Project} />
        <Route path="/table/:id/project/:projectId" component={Table} />
    </Layout>;
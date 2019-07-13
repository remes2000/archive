
(function () {
    'use strict';

    var zapApp = angular.module('zapApp', ['ngRoute']);

    var configFunction = function ($routeProvider, $locationProvider, $httpProvider) {

        $routeProvider
            .when('/home', { templateUrl: '/Views/Home/Home.html', controller: 'homeCtrl' })
            .when('/login', { templateUrl: '/Views/User/Login.html', controller: 'loginCtrl' })
            .when('/register', { templateUrl: '/Views/User/Register.html', controller: 'registerCtrl' })
            .when('/profile', { templateUrl: '/Views/User/Profile.html', controller: 'profileCtrl'})
            .when('/settings', { templateUrl: '/Views/User/Settings.html', controller: 'settingsCtrl' })
            .otherwise({ redirectTo: '/home' });

        $locationProvider.hashPrefix('!');
        $locationProvider.html5Mode(true);
    }

    configFunction.$inject = ['$routeProvider', '$locationProvider'];

    zapApp.config(configFunction);

    zapApp.run(($rootScope, $location) => {
        $rootScope.$on("$locationChangeStart", (event, next, current) => {

            if (!$rootScope.SessionId) {

                switch (next.split('/')[3]) {
                    case 'profile':
                    case 'settings':
                        $location.path('/login')
                    break
                }

            }

        })
    })


    zapApp.run(($rootScope, $http) => {
        $http.defaults.headers.common['SessionId'] = $rootScope.SessionId;
    })
   

})();
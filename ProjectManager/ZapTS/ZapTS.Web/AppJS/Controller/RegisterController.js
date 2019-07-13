(function () {
    'use strict';
    var app=angular.module('zapApp');

    app.controller('registerCtrl', ['$scope', '$http', '$rootScope', function ($scope, $http, $rootScope) {

        $scope.askApiForImportantData = () => {
            $http({
                method: 'GET',
                headers: { 'SessionId': $rootScope.SessionId },
                url: 'http://localhost:51084/api/User/GetImportantData'
            })
            .then(res => {
                console.log('IMPORTANT DATA RESPONSE')
                console.log(res)
             })
        }

        $scope.formSubmit = () => {
            const { Username, Name, Surname, Email, Password } = $scope

            $http({
                method: 'POST',
                url: 'http://localhost:51084/api/User/Register',
                data: {
                    Username,
                    Name,
                    Surname,
                    Email,
                    Password
                }
            })
            .then(res => {
                console.log('REGISTER RESPONSE', res)
             })
        }
    }]);
}());
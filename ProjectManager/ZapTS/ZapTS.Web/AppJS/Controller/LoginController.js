(function () {
    'use strict';
    var app= angular.module('zapApp');

    app.controller('loginCtrl', ['$scope', '$http', '$rootScope', function ($scope, $http, $rootScope) {
        $scope.title = "AngularJS Login Sample";

        $scope.formSubmit = function () {

            $http({
                method: 'POST',
                url: 'http://localhost:51084/api/User/Login',
                data: {
                    Username: $scope.username,
                    Password: $scope.password
                }
            })
            .then(function (response) {
                $rootScope.SessionId = response.data;
                alert('sessionId: ' + $rootScope.SessionId);
                console.log(response.data);
            });
        };
    }]);
}());
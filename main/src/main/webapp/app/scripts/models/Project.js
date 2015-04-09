(function () {
    'use strict';

    angular
        .module('ALEX.models')
        .factory('Project', ProjectModel);

    ProjectModel.$inject = ['ProjectResource'];

    /**
     * The factory for the model of a project.
     *
     * @param ProjectResource - The resource to fetch projects from the server
     * @return {Project}
     * @constructor
     */
    function ProjectModel(ProjectResource) {

        /**
         * The project model.
         *
         * @param name - The name of the project
         * @param baseUrl - The url the project can be called
         * @param description - The description of the project
         * @constructor
         */
        function Project(name, baseUrl, description) {
            this.name = name;
            this.baseUrl = baseUrl;
            this.description = description;
            this.id;
        }

        /**
         * Creates an instance of a project from an object.
         *
         * @param data - The data the project should be build from
         * @return {ProjectModel.Project}
         */
        Project.build = function (data) {
            var project = new Project(data.name, data.baseUrl, data.description);
            project.id = data.id;
            return project;
        };

        /**
         * Creates an array of project instances from an object.
         *
         * @param data
         * @returns {Array}
         */
        Project.buildSome = function (data) {
            var projects = [];
            for (var i = 0; i < data.length; i++) {
                projects.push(Project.build(data[i]));
            }
            return projects;
        };

        /**
         * The resource object for a project
         *
         * @type {ProjectResource}
         */
        Project.Resource = new ProjectResource();

        // attach the build function of the project to the resource so that it can automatically create instances
        // of projects from http responses
        Project.Resource.build = Project.build;
        Project.Resource.buildSome = Project.buildSome;

        return Project;
    }
}());
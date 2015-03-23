(function () {
    'use strict';

    angular
        .module('weblearner.directives')
        .directive('downloadTableAsCsv', downloadTableAsCsv);

    downloadTableAsCsv.$inject = ['FileDownloadService'];

    /**
     * The directive that downloads a HTML table element as CSV. It attaches a click event to the directives element
     * which downloads the file. The directive must be used as an attribute.
     *
     * It expects one attribute 'ancestorOrElement' which should contain the selector to the table or the an ancester
     * of the table.
     *
     * Use it like "<button download-table-as-csv ancestor-or-element="#table">Click Me!</button>"
     *
     * @param FileDownloadService - The service for downloading files
     * @returns {{restrict: string, scope: {ancestorOrElement: string}, link: link}}
     */
    function downloadTableAsCsv(FileDownloadService) {

        // the directive
        return {
            restrict: 'A',
            scope: {
                ancestorOrElement: '@'
            },
            link: link
        };

        // the directives behaviour
        function link(scope, el, attrs) {
            el.on('click', function () {

                // the table element
                var table;
                var csv;

                // find the downloadable table element
                if (scope.ancestorOrElement) {
                    table = document.querySelector(scope.ancestorOrElement);
                    if (table !== null && table.nodeName.toLowerCase() === 'table') {
                        csv = createCSV(table);
                    } else {
                        table = table.querySelector('table');
                        if (table !== null) {
                            csv = createCSV(table);
                        }
                    }

                    // download it
                    if (angular.isDefined(csv)) {
                        FileDownloadService.downloadCSV(csv);
                    }
                }
            });

            /**
             * Creates CSV data from the entries of a HTML table element
             *
             * @param table - The table element that should be converted
             * @returns {string} - The table as CSV string
             */
            function createCSV(table) {
                var head = table.querySelectorAll('thead th');
                var rows = table.querySelectorAll('tbody tr');
                var csv = '';

                // add entries from table head
                if (head.length > 0) {
                    for (var i = 0; i < head.length; i++) {
                        csv += head[i].textContent.replace(',', ' ') + (i === head.length - 1 ? '\n' : ',');
                    }
                }

                // add entries from table row
                if (rows.length > 0) {
                    for (var i = 0; i < rows.length; i++) {
                        var tds = rows[i].querySelectorAll('td');
                        if (tds.length > 0) {
                            for (var j = 0; j < tds.length; j++) {
                                csv += tds[j].textContent.replace(',', ' ') + (j === tds.length - 1 ? '\n' : ',');
                            }
                        }
                    }
                }

                return csv;
            }
        }
    }
}());
/**
 * The directive that displays the learn result list
 * @returns {{transclude: boolean, template: string}}
 */
function learnResultList() {
    return {
        transclude: true,
        template: '<div class="learn-result-list" ng-transclude></div>'
    };
}

/**
 * The directive that displays a learn result list item
 *
 * Use: <learn-result-list-item result="..."></learn-result-list-item> where
 * 'result' should be a learn result object.
 *
 * Everything that is put between the tags is displayed at the most right.
 *
 * @returns {{transclude: boolean, scope: {result: string}, template: string}}
 */
function learnResultListItem() {
    return {
        transclude: true,
        scope: {
            result: '=',
            linkTo: '@'
        },
        template: `
                <div class="learn-result-list-item">
                    <checkbox model="result" class="pull-left" selection-model-ignore></checkbox>
                    <div class="learn-result-list-item-content">
                        <div class="pull-right" ng-transclude></div>
                        <span class="label label-danger pull-right" ng-show="result.error">Failed</span>
                        <strong>
                            <a ui-sref="learn.results.compare({testNos: [result.testNo]})" ng-if="linkTo === 'hypothesis'">
                                Test No <span ng-bind="result.testNo"></span>
                            </a>
                            <a ui-sref="statistics.compare({testNos: [result.testNo], mode: 'cumulated'})" ng-if="linkTo === 'statistics'">
                                Test No <span ng-bind="result.testNo"></span>
                            </a>
                        </strong>,
                        [<span ng-bind="(result.configuration.algorithm|formatAlgorithm)"></span>]
                        <div class="text-muted">
                        <em>
                            Started: <span ng-bind="(result.statistics.startDate | date : 'EEE, dd.MM.yyyy, HH:mm')"></span>
                        </em>
                    </div>
                    <div class="comment text-muted" ng-show="result.configuration.comment"  ng-bind="result.configuration.comment">
                    </div>
                </div>
            </div>
            `
    };
}

export {learnResultList, learnResultListItem};
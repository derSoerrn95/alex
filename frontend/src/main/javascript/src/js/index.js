/*
 * Copyright 2018 TU Dortmund
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import uiRouter from '@uirouter/angularjs';
import angular from 'angular';
import ngAnimate from 'angular-animate';
import angularDragula from 'angular-dragula';
import angularJwt from 'angular-jwt';
import ngMessages from 'angular-messages';
import toastr from 'angular-toastr';
import uiBootstrap from 'angular-ui-bootstrap';
import ngFileUpload from 'ng-file-upload';
import {actionBarComponent} from './components/action-bar/action-bar.component';
import {actionRecorderComponent} from './components/action-recorder/action-recorder.component';
import {alexComponent} from './components/alex/alex.component';
import {discriminationTreeComponent} from './components/discrimination-tree/discrimination-tree.component';
import {fileDropzoneComponent} from './components/file-dropzone/file-dropzone.component';
import {actionFormComponent} from './components/forms/actions/action-form/action-form.component';
import {assertCounterActionFormComponent} from './components/forms/actions/misc/assert-counter-action-form/assert-counter-action-form.component';
import {assertVariableActionFormComponent} from './components/forms/actions/misc/assert-variable-action-form/assert-variable-action-form.component';
import {incrementCounterActionFormComponent} from './components/forms/actions/misc/increment-counter-action-form/increment-counter-action-form.component';
import {setCounterActionFormComponent} from './components/forms/actions/misc/set-counter-action-form/set-counter-action-form.component';
import {setVariableActionFormComponent} from './components/forms/actions/misc/set-variable-action-form/set-variable-action-form.component';
import {setVariableByCookieActionFormComponent} from './components/forms/actions/misc/set-variable-by-cookie-action-form/set-variable-by-cookie-action-form.component';
import {setVariableByHtmlActionFormComponent} from './components/forms/actions/misc/set-variable-by-html-action-form/set-variable-by-html-action-form.component';
import {setVariableByHttpResponseActionFormComponent} from './components/forms/actions/misc/set-variable-by-http-response-action-form/set-variable-by-http-response-action-form.component';
import {setVariableByJsonActionFormComponent} from './components/forms/actions/misc/set-variable-by-json-action-form/set-variable-by-json-action-form.component';
import {setVariableByNodeAttributeActionFormComponent} from './components/forms/actions/misc/set-variable-by-node-attribute-action-form/set-variable-by-node-attribute-action-form.component';
import {setVariableByNodeCountActionFormComponent} from './components/forms/actions/misc/set-variable-by-node-count-action-form/set-variable-by-node-count-action-form.component';
import {setVariableByRegexGroupActionFormComponent} from './components/forms/actions/misc/set-variable-by-regex-group-action-form/set-variable-by-regex-group-action-form.component';
import {waitActionFormComponent} from './components/forms/actions/misc/wait-action-form/wait-action-form.component';
import {checkAttributeExistsActionFormComponent} from './components/forms/actions/rest/check-attribute-exists-action-form/check-attribute-exists-action-form.component';
import {checkAttributeTypeActionFormComponent} from './components/forms/actions/rest/check-attribute-type-action-form/check-attribute-type-action-form.component';
import {checkAttributeValueActionFormComponent} from './components/forms/actions/rest/check-attribute-value-action-form/check-attribute-value-action-form.component';
import {checkHeaderFieldActionFormComponent} from './components/forms/actions/rest/check-header-field-action-form/check-header-field-action-form.component';
import {checkHttpBodyActionFormComponent} from './components/forms/actions/rest/check-http-body-action-form/check-http-body-action-form.component';
import {checkStatusActionFormComponent} from './components/forms/actions/rest/check-status-action-form/check-status-action-form.component';
import {requestActionFormComponent} from './components/forms/actions/rest/request-action-form/request-action-form.component';
import {validateJsonActionFormComponent} from './components/forms/actions/rest/validate-json-action-form/validate-json-action-form.component';
import {alertAcceptDismissActionFormComponent} from './components/forms/actions/web/alert-accept-dismiss-action-form/alert-accept-dismiss-action-form.component';
import {alertGetTextActionFormComponent} from './components/forms/actions/web/alert-get-text-action-form/alert-get-text-action-form.component';
import {alertSendKeysActionFormComponent} from './components/forms/actions/web/alert-send-keys-action-form/alert-send-keys-action-form.component';
import {browserActionFormComponent} from './components/forms/actions/web/browser-action-form/browser-action-form.component';
import {checkForNodeActionFormComponent} from './components/forms/actions/web/check-for-node-action-form/check-for-node-action-form.component';
import {checkForTextActionFormComponent} from './components/forms/actions/web/check-for-text-action-form/check-for-text-action-form.component';
import {checkNodeAttributeValueActionFormComponent} from './components/forms/actions/web/check-node-attribute-value-action-form/check-node-attribute-value-action-form.component';
import {checkNodeSelectedActionFormComponent} from './components/forms/actions/web/check-node-selected-action-form/check-node-selected-action-form.component';
import {checkPageTitleActionFormComponent} from './components/forms/actions/web/check-page-title-action-form/check-page-title-action-form.component';
import {clearInputActionFormComponent} from './components/forms/actions/web/clear-action-form/clear-action-form.component';
import {clickActionFormComponent} from './components/forms/actions/web/click-action-form/click-action-form.component';
import {clickElementByTextActionFormComponent} from './components/forms/actions/web/click-element-by-text-action-form/click-element-by-text-action-form.component';
import {clickLinkByTextActionFormComponent} from './components/forms/actions/web/click-link-by-text-action-form/click-link-by-text-action-form.component';
import {executeScriptActionFormComponent} from './components/forms/actions/web/execute-script-action-form/execute-script-action-form.component';
import {moveMouseActionFormComponent} from './components/forms/actions/web/move-mouse-action-form/move-mouse-action-form.component';
import {openActionFormComponent} from './components/forms/actions/web/open-url-action-form/open-url-action-form.component';
import {pressKeyActionFormComponent} from './components/forms/actions/web/press-key-action-form/press-key-action-form.component';
import {selectActionFormComponent} from './components/forms/actions/web/select-action-form/select-action-form.component';
import {sendKeysActionFormComponent} from './components/forms/actions/web/send-keys-action-form/send-keys-action-form.component';
import {submitActionFormComponent} from './components/forms/actions/web/submit-action-form/submit-action-form.component';
import {switchToFrameActionFormComponent} from './components/forms/actions/web/switch-to-frame/switch-to-frame-action-form.component';
import {switchToActionFormComponent} from './components/forms/actions/web/switch-to/switch-to-action-form.component';
import {waitForNodeActionFormComponent} from './components/forms/actions/web/wait-for-node-action-form/wait-for-node-action-form.component';
import {waitForNodeAttributeActionFormComponent} from './components/forms/actions/web/wait-for-node-attribute-action-form/wait-for-node-attribute-action-form.component';
import {waitForTextActionFormComponent} from './components/forms/actions/web/wait-for-text-action-form/wait-for-text-action-form.component';
import {waitForTitleActionFormComponent} from './components/forms/actions/web/wait-for-title-action-form/wait-for-title-action-form.component';
import {browserConfigFormComponent} from './components/forms/browser-config-form/browser-config-form.component';
import {completeEqOracleFormComponent} from './components/forms/eq-oracles/complete-eq-oracle-form/complete-eq-oracle-form.component';
import {eqOracleFormComponent} from './components/forms/eq-oracles/eq-oracle-form.component';
import {hypothesisEqOracleFormComponent} from './components/forms/eq-oracles/hypothesis-eq-oracle-form/hypothesis-eq-oracle-form.component';
import {randomEqOracleFormComponent} from './components/forms/eq-oracles/random-eq-oracle-form/random-eq-oracle-form.component';
import {wMethodEqOracleFormComponent} from './components/forms/eq-oracles/w-method-eq-oracle-form/w-method-eq-oracle-form.component';
import {nodeFormGroupComponent} from './components/forms/node-form-group/node-form-group.component';
import {projectCreateFormComponent} from './components/forms/project-create-form/project-create-form.component';
import {projectFormGroupsComponent} from './components/forms/project-form-groups/project-form-groups.component';
import {projectFormUrlGroupsComponent} from './components/forms/project-form-groups/project-form-url-groups/project-form-url-groups.component';
import {symbolEditFormComponent} from './components/forms/symbol-edit-form/symbol-edit-form.component';
import {symbolFormGroupsComponent} from './components/forms/symbol-form-groups/symbol-form-groups.component';
import {symbolParameterFormGroupsComponent} from './components/forms/symbol-parameter-form-groups/symbol-parameter-form-groups.components';
import {userEditFormComponent} from './components/forms/user-edit-form/user-edit-form.component';
import {userLoginFormComponent} from './components/forms/user-login-form/user-login-form.component';
import {webhookFormComponent} from './components/forms/webhook-form/webhook-form.component';
import {htmlElementPickerComponent} from './components/html-element-picker/html-element-picker.component';
import {hypothesisComponent} from './components/hypothesis/hypothesis.component';
import {symbolsExportModalComponent} from './components/modals/symbols-export-modal/symbols-export-modal.component';
import {userCreateModalComponent} from './components/modals/user-create-modal/user-create-modal.component';
import {learnerResultListItemComponent} from './components/views/learner-results-view/learner-result-list-item/learner-result-list-item.component';
import {learnerResultPanelComponent} from './components/learner-result-panel/learner-result-panel.component';
import {loadScreenComponent} from './components/load-screen/load-screen.component';
import {actionCreateModalHandleDirective} from './components/modals/action-create-modal/action-create-modal-handle.directive';
import {actionCreateModalComponent} from './components/modals/action-create-modal/action-create-modal.component';
import {actionSearchFormComponent} from './components/modals/action-create-modal/action-search-form/action-search-form.component';
import {actionEditModalHandleDirective} from './components/modals/action-edit-modal/action-edit-modal-handle.directive';
import {actionEditModalComponent} from './components/modals/action-edit-modal/action-edit-modal.component';
import {actionRecorderActionsModal} from './components/modals/action-recorder-actions-modal/action-recorder-actions-modal.component';
import {confirmModalComponent} from './components/modals/confirm-modal/confirm-modal.component';
import {counterCreateModalComponent} from './components/modals/counter-create-modal/counter-create-modal.component';
import {hypothesisLayoutSettingsModalHandleDirective} from './components/modals/hypothesis-layout-settings-modal/hypothesis-layout-settings-modal-handle.directive';
import {hypothesisLayoutSettingsModalComponent} from './components/modals/hypothesis-layout-settings-modal/hypothesis-layout-settings-modal.component';
import {learnerResultDetailsModalHandleDirective} from './components/modals/learner-result-details-modal/learner-result-details-modal-handle.directive';
import {learnerResultDetailsModalComponent} from './components/modals/learner-result-details-modal/learner-result-details-modal.component';
import {resultListModalHandleDirective} from './components/modals/learner-result-list-modal/learner-result-list-modal-handle.directive';
import {resultListModalComponent} from './components/modals/learner-result-list-modal/learner-result-list-modal.component';
import {learnerSetupSettingsModalHandleDirective} from './components/modals/learner-setup-settings-modal/learner-setup-settings-modal-handle.directive';
import {learnerSetupSettingsModalComponent} from './components/modals/learner-setup-settings-modal/learner-setup-settings-modal.component';
import {projectEditModalHandleDirective} from './components/modals/project-edit-modal/project-edit-modal-handle.directive';
import {projectEditModalComponent} from './components/modals/project-edit-modal/project-edit-modal.component';
import {promptModalComponent} from './components/modals/prompt-modal/prompt-modal.component';
import {separatingWordModalComponent} from './components/modals/separating-word-modal/separating-word-modal.component';
import {symbolCreateModalHandleDirective} from './components/modals/symbol-create-modal/symbol-create-modal-handle.directive';
import {symbolCreateModalComponent} from './components/modals/symbol-create-modal/symbol-create-modal.component';
import {symbolEditModalHandleDirective} from './components/modals/symbol-edit-modal/symbol-edit-modal-handle.directive';
import {symbolEditModalComponent} from './components/modals/symbol-edit-modal/symbol-edit-modal.component';
import {symbolGroupCreateModalHandleDirective} from './components/modals/symbol-group-create-modal/symbol-group-create-modal-handle.directive';
import {symbolGroupCreateModalComponent} from './components/modals/symbol-group-create-modal/symbol-group-create-modal.component';
import {symbolGroupEditModalComponent} from './components/modals/symbol-group-edit-modal/symbol-group-edit-modal.component';
import {symbolGroupMoveModalComponent} from './components/modals/symbol-group-move-modal/symbol-group-move-modal.component';
import {symbolParameterCreateModalComponent} from './components/modals/symbol-parameter-create-modal/symbol-parameter-create-modal.component';
import {symbolParameterEditModalComponent} from './components/modals/symbol-parameter-edit-modal/symbol-parameter-edit-modal.component';
import {symbolsImportModalComponent} from './components/modals/symbols-import-modal/symbols-import-modal.component';
import {symbolMoveModalHandleDirective} from './components/modals/symbols-move-modal/symbols-move-modal-handle.directive';
import {symbolMoveModalComponent} from './components/modals/symbols-move-modal/symbols-move-modal.component';
import {testConfigModalComponent} from './components/modals/test-config-modal/test-config-modal.component';
import {testsImportModalComponent} from './components/modals/tests-import-modal/tests-import-modal.component';
import {testSuiteTreeComponent} from './components/modals/tests-move-modal/test-suite-tree-component/test-suite-tree.component';
import {testsMoveModalComponent} from './components/modals/tests-move-modal/tests-move-modal.component';
import {userEditModalComponent} from './components/modals/user-edit-modal/user-edit-modal.component';
import {webhookCreateModalComponent} from './components/modals/webhook-create-modal/webhook-create-modal.component';
import {webhookEditModalComponent} from './components/modals/webhook-edit-modal/webhook-edit-modal.component';
import {observationTableComponent} from './components/observation-table/observation-table.component';
import {projectListComponent} from './components/project-list/project-list.component';
import {projectUrlListComponent} from './components/project-url-list/project-url-list.component';
import {responsiveIframeComponent} from './components/responsive-iframe/responsive-iframe.component';
import {searchFormComponent} from './components/search-form/search-form.component';
import {selectableCheckboxMultipleComponent} from './components/selectable-checkbox-multiple/selectable-checkbox-multiple.component';
import {selectableCheckboxComponent} from './components/selectable-checkbox/selectable-checkbox.component';
import {sidebarComponent} from './components/sidebar/sidebar.component';
import {simpleSymbolGroupTreeItemComponent} from './components/simple-symbol-group-tree/simple-symbol-group-tree-item/simple-symbol-group-tree-item.component';
import {simpleSymbolGroupTreeComponent} from './components/simple-symbol-group-tree/simple-symbol-group-tree.component';
import {symbolGroupHeaderComponent} from './components/symbol-group-tree/symbol-group-header/symbol-group-header.component';
import {symbolItemComponent} from './components/symbol-group-tree/symbol-item/symbol-item.component';
import {symbolListItemComponent} from './components/symbol-list-item/symbol-list-item.component';
import {symbolParametersPanelComponent} from './components/symbol-parameters-panel/symbol-parameters-panel.component';
import {testConfigListComponent} from './components/test-config-list/test-config-list.component';
import {testResultReportComponent} from './components/test-result-report/test-result-report.component';
import {testCaseNodeComponent} from './components/test-tree/test-case-node/test-case-node.component';
import {testSuiteNodeComponent} from './components/test-tree/test-suite-node/test-suite-node.component';
import {testTreeComponent} from './components/test-tree/test-tree.component';
import {viewHeaderComponent} from './components/view-header/view-header.component';
import {aboutViewComponent} from './components/views/about-view/about-view.component';
import {adminSettingsViewComponent} from './components/views/admin-settings-view/admin-settings-view.component';
import {adminUsersViewComponent} from './components/views/admin-users-view/admin-users-view.component';
import {countersViewComponent} from './components/views/counters-view/counters-view.component';
import {errorViewComponent} from './components/views/error-view/error-view.component';
import {filesViewComponent} from './components/views/files-view/files-view.component';
import {resultsCompareViewComponent} from './components/views/learner-results-compare-view/learner-results-compare-view.component';
import {resultsViewComponent} from './components/views/learner-results-view/learner-results-view.component';
import {learnerSetupSymbolGroupTreeComponent} from './components/views/learner-setup-view/learner-setup-symbol-group-tree/learner-setup-symbol-group-tree.component';
import {learnerSetupViewComponent} from './components/views/learner-setup-view/learner-setup-view.component';
import {learnerViewComponent} from './components/views/learner-view/learner-view.component';
import {profileViewComponent} from './components/views/profile-view/profile-view.component';
import {projectViewComponent} from './components/views/project-view/project-view.component';
import {projectsViewComponent} from './components/views/projects-view/projects-view.component';
import {redirectViewComponent} from './components/views/redirect-view/redirect-view.component';
import {rootViewComponent} from './components/views/root-view/root-view.component';
import {statisticsCompareViewComponent} from './components/views/statistics-compare-view/statistics-compare-view.component';
import {symbolViewComponent} from './components/views/symbol-view/symbol-view.component';
import {symbolsArchiveViewComponent} from './components/views/symbols-archive-view/symbols-archive-view.component';
import {symbolSearchFormComponent} from './components/views/symbols-view/symbol-search-form/symbol-search-form.component';
import {symbolsSymbolGroupTreeComponent} from './components/views/symbols-view/symbols-symbol-group-tree/symbols-symbol-group-tree.component';
import {symbolsViewComponent} from './components/views/symbols-view/symbols-view.component';
import {prePostTestCaseStepComponent} from './components/views/test-case-view/pre-post-test-case-step/pre-post-test-case-step.component';
import {testCaseViewComponent} from './components/views/test-case-view/test-case-view.component';
import {reportChartsComponent} from './components/views/test-report-view/report-donut-chart/report-charts.component';
import {reportOutputsColumnComponent} from './components/views/test-report-view/report-output-column/report-outputs-column.component';
import {testReportViewComponent} from './components/views/test-report-view/test-report-view.component';
import {testReportsViewComponent} from './components/views/test-reports-view/test-reports-view.component';
import {testSuiteViewComponent} from './components/views/test-suite-view/test-suite-view.component';
import {testsViewComponent} from './components/views/tests-view/tests-view.component';
import {webhooksViewComponent} from './components/views/webhooks-view/webhooks-view.component';
import {counterexamplesWidgetComponent} from './components/widgets/counterexamples-widget/counterexamples-widget.component';
import {latestLearnerResultWidgetComponent} from './components/widgets/latest-learner-result-widget/latest-learner-result-widget.component';
import {latestTestReportWidgetComponent} from './components/widgets/latest-test-report-widget/latest-test-report-widget.component';
import {learnerResumeSettingsWidgetComponent} from './components/widgets/learner-resume-widget/learner-resume-settings-widget.component';
import {learnerStatusWidgetComponent} from './components/widgets/learner-status-widget/learner-status-widget.component';
import {projectDetailsWidgetComponent} from './components/widgets/project-details-widget/project-details-widget.component';
import {widgetComponent} from './components/widgets/widget/widget.component';
import * as config from './config';
import * as constant from './constants';
import {
    formatAlgorithm,
    formatEqOracle,
    formatMilliseconds,
    formatParameterType,
    formatUserRole,
    formatWebBrowser,
    normalizeUpperCase,
    sortTests
} from './filters';
import * as routes from './routes';
import {ActionRecorderService} from './services/action-recorder.service';
import {ActionService} from './services/action.service';
import {ClipboardService} from './services/clipboard.service';
import {DownloadService} from './services/download.service';
import {EqOracleService} from './services/eq-oracle.service';
import {EventBus} from './services/eventbus.service';
import {HtmlElementPickerService} from './services/html-element-picker.service';
import {LearnerResultChartService} from './services/learner-result-chart.service';
import {LearnerResultDownloadService} from './services/learner-result-download.service';
import {LearningAlgorithmService} from './services/learning-algorithm.service';
import {NotificationService} from './services/notification.service';
import {PromptService} from './services/prompt.service';
import {CounterResource} from './services/resources/counter-resource.service';
import {FileResource} from './services/resources/file-resource.service';
import {LearnerResource} from './services/resources/learner-resource.service';
import {LearnResultResource} from './services/resources/learner-result-resource.service';
import {ProjectResource} from './services/resources/project-resource.service';
import {SettingsResource} from './services/resources/settings-resource.service';
import {SymbolGroupResource} from './services/resources/symbol-group-resource.service';
import {SymbolParameterResource} from './services/resources/symbol-parameter-resource.service';
import {SymbolResource} from './services/resources/symbol-resource.service';
import {TestConfigResource} from './services/resources/test-config-resource.service';
import {TestReportResource} from './services/resources/test-report-resource.service';
import {TestResource} from './services/resources/test-resource.service';
import {UserResource} from './services/resources/user-resource.service';
import {WebhookResource} from './services/resources/webhook-resource.service';
import {SessionService} from './services/session.service';
import {TestReportService} from './services/test-report.service';
import {TestService} from './services/test.service';
import {ToastService} from './services/toast.service';

angular
    .module('ALEX', [

        // modules from external libraries
        ngAnimate,
        ngMessages,
        uiBootstrap,
        uiRouter,
        'ui.ace',
        'n3-line-chart',
        toastr,
        angularJwt,
        ngFileUpload,
        angularDragula(angular)
    ])

    .config(config.config)
    .config(routes.config)
    .run(routes.run)

    // constants
    .constant('learnAlgorithm', constant.learnAlgorithm)
    .constant('webBrowser', constant.webBrowser)
    .constant('eqOracleType', constant.eqOracleType)
    .constant('events', constant.events)
    .constant('actionType', constant.actionType)

    // filters
    .filter('formatEqOracle', formatEqOracle)
    .filter('formatAlgorithm', formatAlgorithm)
    .filter('formatMilliseconds', formatMilliseconds)
    .filter('formatUserRole', formatUserRole)
    .filter('formatWebBrowser', formatWebBrowser)
    .filter('sortTests', sortTests)
    .filter('formatParameterType', formatParameterType)
    .filter('normalizeUpperCase', normalizeUpperCase)

    // resources
    .service('CounterResource', CounterResource)
    .service('FileResource', FileResource)
    .service('LearnerResource', LearnerResource)
    .service('LearnResultResource', LearnResultResource)
    .service('ProjectResource', ProjectResource)
    .service('SettingsResource', SettingsResource)
    .service('SymbolGroupResource', SymbolGroupResource)
    .service('SymbolParameterResource', SymbolParameterResource)
    .service('SymbolResource', SymbolResource)
    .service('UserResource', UserResource)
    .service('TestConfigResource', TestConfigResource)
    .service('TestResource', TestResource)
    .service('TestReportResource', TestReportResource)
    .service('WebhookResource', WebhookResource)

    // services
    .service('ActionService', ActionService)
    .service('ClipboardService', ClipboardService)
    .service('EventBus', EventBus)
    .service('EqOracleService', EqOracleService)
    .service('LearningAlgorithmService', LearningAlgorithmService)
    .service('DownloadService', DownloadService)
    .service('LearnerResultChartService', LearnerResultChartService)
    .service('PromptService', PromptService)
    .service('SessionService', SessionService)
    .service('ToastService', ToastService)
    .service('LearnerResultDownloadService', LearnerResultDownloadService)
    .service('HtmlElementPickerService', HtmlElementPickerService)
    .service('ActionRecorderService', ActionRecorderService)
    .service('TestService', TestService)
    .service('TestReportService', TestReportService)
    .service('NotificationService', NotificationService)

    // modal handles
    .directive('actionCreateModalHandle', actionCreateModalHandleDirective)
    .directive('actionEditModalHandle', actionEditModalHandleDirective)
    .directive('hypothesisLayoutSettingsModalHandle', hypothesisLayoutSettingsModalHandleDirective)
    .directive('learnerResultDetailsModalHandle', learnerResultDetailsModalHandleDirective)
    .directive('learnerSetupSettingsModalHandle', learnerSetupSettingsModalHandleDirective)
    .directive('projectEditModalHandle', projectEditModalHandleDirective)
    .directive('symbolCreateModalHandle', symbolCreateModalHandleDirective)
    .directive('symbolEditModalHandle', symbolEditModalHandleDirective)
    .directive('symbolGroupCreateModalHandle', symbolGroupCreateModalHandleDirective)
    .directive('symbolMoveModalHandle', symbolMoveModalHandleDirective)
    .directive('resultListModalHandle', resultListModalHandleDirective)

    // modals
    .component('actionCreateModal', actionCreateModalComponent)
    .component('actionRecorderActionsModal', actionRecorderActionsModal)
    .component('actionEditModal', actionEditModalComponent)
    .component('counterCreateModal', counterCreateModalComponent)
    .component('hypothesisLayoutSettingsModal', hypothesisLayoutSettingsModalComponent)
    .component('learnerResultDetailsModal', learnerResultDetailsModalComponent)
    .component('learnerSetupSettingsModal', learnerSetupSettingsModalComponent)
    .component('projectEditModal', projectEditModalComponent)
    .component('symbolCreateModal', symbolCreateModalComponent)
    .component('symbolEditModal', symbolEditModalComponent)
    .component('symbolGroupCreateModal', symbolGroupCreateModalComponent)
    .component('symbolGroupEditModal', symbolGroupEditModalComponent)
    .component('symbolMoveModal', symbolMoveModalComponent)
    .component('symbolsExportModal', symbolsExportModalComponent)
    .component('userCreateModal', userCreateModalComponent)
    .component('userEditModal', userEditModalComponent)
    .component('resultListModal', resultListModalComponent)
    .component('symbolsImportModal', symbolsImportModalComponent)
    .component('promptModal', promptModalComponent)
    .component('confirmModal', confirmModalComponent)
    .component('testConfigModal', testConfigModalComponent)
    .component('testsImportModal', testsImportModalComponent)
    .component('webhookCreateModal', webhookCreateModalComponent)
    .component('webhookEditModal', webhookEditModalComponent)
    .component('symbolParameterCreateModal', symbolParameterCreateModalComponent)
    .component('symbolParameterEditModal', symbolParameterEditModalComponent)
    .component('symbolGroupMoveModal', symbolGroupMoveModalComponent)
    .component('separatingWordModal', separatingWordModalComponent)
    .component('testsMoveModal', testsMoveModalComponent)

    // view components
    .component('aboutView', aboutViewComponent)
    .component('adminSettingsView', adminSettingsViewComponent)
    .component('adminUsersView', adminUsersViewComponent)
    .component('countersView', countersViewComponent)
    .component('errorView', errorViewComponent)
    .component('filesView', filesViewComponent)
    .component('rootView', rootViewComponent)
    .component('learnerSetupView', learnerSetupViewComponent)
    .component('learnerView', learnerViewComponent)
    .component('projectsView', projectsViewComponent)
    .component('projectView', projectViewComponent)
    .component('resultsCompareView', resultsCompareViewComponent)
    .component('resultsView', resultsViewComponent)
    .component('statisticsCompareView', statisticsCompareViewComponent)
    .component('symbolView', symbolViewComponent)
    .component('symbolsView', symbolsViewComponent)
    .component('testsView', testsViewComponent)
    .component('symbolsArchiveView', symbolsArchiveViewComponent)
    .component('profileView', profileViewComponent)
    .component('testCaseView', testCaseViewComponent)
    .component('testReportsView', testReportsViewComponent)
    .component('testReportView', testReportViewComponent)
    .component('testSuiteView', testSuiteViewComponent)
    .component('webhooksView', webhooksViewComponent)
    .component('redirectView', redirectViewComponent)

    // forms components
    .component('actionForm', actionFormComponent)
    .component('projectCreateForm', projectCreateFormComponent)
    .component('projectFormGroups', projectFormGroupsComponent)
    .component('projectFormUrlGroups', projectFormUrlGroupsComponent)
    .component('userEditForm', userEditFormComponent)
    .component('userLoginForm', userLoginFormComponent)
    .component('nodeFormGroup', nodeFormGroupComponent)
    .component('browserConfigForm', browserConfigFormComponent)
    .component('symbolEditForm', symbolEditFormComponent)
    .component('symbolFormGroups', symbolFormGroupsComponent)
    .component('searchForm', searchFormComponent)
    .component('actionSearchForm', actionSearchFormComponent)
    .component('symbolSearchForm', symbolSearchFormComponent)
    .component('webhookForm', webhookFormComponent)
    .component('symbolParameterFormGroups', symbolParameterFormGroupsComponent)
    .component('eqOracleForm', eqOracleFormComponent)
    .component('completeEqOracleForm', completeEqOracleFormComponent)
    .component('randomEqOracleForm', randomEqOracleFormComponent)
    .component('wMethodEqOracleForm', wMethodEqOracleFormComponent)
    .component('hypothesisEqOracleForm', hypothesisEqOracleFormComponent)

    // widgets components
    .component('widget', widgetComponent)
    .component('counterexamplesWidget', counterexamplesWidgetComponent)
    .component('learnResumeSettingsWidget', learnerResumeSettingsWidgetComponent)
    .component('learnerStatusWidget', learnerStatusWidgetComponent)
    .component('latestLearnResultWidget', latestLearnerResultWidgetComponent)
    .component('projectDetailsWidget', projectDetailsWidgetComponent)
    .component('latestTestReportWidget', latestTestReportWidgetComponent)

    // web action forms
    .component('alertAcceptDismissActionForm', alertAcceptDismissActionFormComponent)
    .component('alertGetTextActionForm', alertGetTextActionFormComponent)
    .component('alertSendKeysActionForm', alertSendKeysActionFormComponent)
    .component('browserActionForm', browserActionFormComponent)
    .component('checkNodeAttributeValueActionForm', checkNodeAttributeValueActionFormComponent)
    .component('checkForNodeActionForm', checkForNodeActionFormComponent)
    .component('checkForTextActionForm', checkForTextActionFormComponent)
    .component('checkPageTitleActionForm', checkPageTitleActionFormComponent)
    .component('checkNodeSelectedActionForm', checkNodeSelectedActionFormComponent)
    .component('clearInputActionForm', clearInputActionFormComponent)
    .component('clickActionForm', clickActionFormComponent)
    .component('clickElementByTextActionForm', clickElementByTextActionFormComponent)
    .component('clickLinkByTextActionForm', clickLinkByTextActionFormComponent)
    .component('executeScriptActionForm', executeScriptActionFormComponent)
    .component('moveMouseActionForm', moveMouseActionFormComponent)
    .component('openActionForm', openActionFormComponent)
    .component('selectActionForm', selectActionFormComponent)
    .component('sendKeysActionForm', sendKeysActionFormComponent)
    .component('submitActionForm', submitActionFormComponent)
    .component('switchToActionForm', switchToActionFormComponent)
    .component('switchToFrameActionForm', switchToFrameActionFormComponent)
    .component('waitForNodeActionForm', waitForNodeActionFormComponent)
    .component('waitForTitleActionForm', waitForTitleActionFormComponent)
    .component('waitForTextActionForm', waitForTextActionFormComponent)
    .component('pressKeyActionForm', pressKeyActionFormComponent)
    .component('waitForNodeAttributeActionForm', waitForNodeAttributeActionFormComponent)

    // rest action forms
    .component('requestActionForm', requestActionFormComponent)
    .component('checkAttributeExistsActionForm', checkAttributeExistsActionFormComponent)
    .component('checkAttributeTypeActionForm', checkAttributeTypeActionFormComponent)
    .component('checkAttributeValueActionForm', checkAttributeValueActionFormComponent)
    .component('checkHeaderFieldActionForm', checkHeaderFieldActionFormComponent)
    .component('checkHttpBodyActionForm', checkHttpBodyActionFormComponent)
    .component('checkStatusActionForm', checkStatusActionFormComponent)
    .component('validateJsonActionForm', validateJsonActionFormComponent)

    // misc action forms
    .component('assertCounterActionForm', assertCounterActionFormComponent)
    .component('assertVariableActionForm', assertVariableActionFormComponent)
    .component('incrementCounterActionForm', incrementCounterActionFormComponent)
    .component('setCounterActionForm', setCounterActionFormComponent)
    .component('setVariableActionForm', setVariableActionFormComponent)
    .component('setVariableByCookieActionForm', setVariableByCookieActionFormComponent)
    .component('setVariableByHttpResponseActionForm', setVariableByHttpResponseActionFormComponent)
    .component('setVariableByHtmlActionForm', setVariableByHtmlActionFormComponent)
    .component('setVariableByJsonActionForm', setVariableByJsonActionFormComponent)
    .component('setVariableByNodeAttributeActionForm', setVariableByNodeAttributeActionFormComponent)
    .component('setVariableByNodeCountActionForm', setVariableByNodeCountActionFormComponent)
    .component('setVariableByRegexGroupActionForm', setVariableByRegexGroupActionFormComponent)
    .component('waitActionForm', waitActionFormComponent)

    // misc components
    .component('alex', alexComponent)
    .component('actionBar', actionBarComponent)
    .component('hypothesis', hypothesisComponent)
    .component('discriminationTree', discriminationTreeComponent)
    .component('fileDropzone', fileDropzoneComponent)
    .component('loadScreen', loadScreenComponent)
    .component('projectList', projectListComponent)
    .component('sidebar', sidebarComponent)
    .component('responsiveIframe', responsiveIframeComponent)
    .component('viewHeader', viewHeaderComponent)
    .component('htmlElementPicker', htmlElementPickerComponent)
    .component('actionRecorder', actionRecorderComponent)
    .component('learnerResultPanel', learnerResultPanelComponent)
    .component('observationTable', observationTableComponent)
    .component('symbolListItem', symbolListItemComponent)
    .component('learnerResultListItem', learnerResultListItemComponent)
    .component('testResultReport', testResultReportComponent)
    .component('testTree', testTreeComponent)
    .component('testCaseNode', testCaseNodeComponent)
    .component('symbolParametersPanel', symbolParametersPanelComponent)
    .component('testSuiteNode', testSuiteNodeComponent)
    .component('projectUrlList', projectUrlListComponent)
    .component('simpleSymbolGroupTree', simpleSymbolGroupTreeComponent)
    .component('simpleSymbolGroupTreeItem', simpleSymbolGroupTreeItemComponent)
    .component('symbolGroupHeader', symbolGroupHeaderComponent)
    .component('symbolItem', symbolItemComponent)
    .component('symbolsSymbolGroupTree', symbolsSymbolGroupTreeComponent)
    .component('reportOutputsColumn', reportOutputsColumnComponent)
    .component('reportCharts', reportChartsComponent)
    .component('learnerSetupSymbolGroupTree', learnerSetupSymbolGroupTreeComponent)
    .component('testConfigList', testConfigListComponent)
    .component('selectableCheckbox', selectableCheckboxComponent)
    .component('selectableCheckboxMultiple', selectableCheckboxMultipleComponent)
    .component('prePostTestCaseStep', prePostTestCaseStepComponent)
    .component('testSuiteTree', testSuiteTreeComponent);

angular.bootstrap(document, ['ALEX']);

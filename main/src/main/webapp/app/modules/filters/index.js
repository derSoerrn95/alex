import * as filter from './filters';

const moduleName = 'ALEX.filters';

angular
    .module(moduleName, [])
    .filter('formatEnumKey', filter.formatEnumKey)
    .filter('formatEqOracle', filter.formatEqOracle)
    .filter('formatAlgorithm', filter.formatAlgorithm)
    .filter('formatMilliseconds', filter.formatMilliseconds)
    .filter('formatWebBrowser', filter.formatWebBrowser);

export const filters = moduleName;
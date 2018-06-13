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

/**
 * Utilities for dates.
 */
export class DateUtils {

    /**
     * Get the current date as YYYYMMDD.
     *
     * @return {string}
     */
    static YYYYMMDD() {
        const date = new Date();

        let month = date.getMonth() + 1;
        month = month < 10 ? '0' + month : '' + month;

        let day = date.getDate();
        day = day < 10 ? '0' + day : '' + day;

        return '' + date.getFullYear() + month + day;
    }
}

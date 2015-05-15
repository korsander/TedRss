/**
 * Created by korsander on 04.05.2015.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.korsander.tedrss.utils;

import ru.korsander.tedrss.db.TedRssDBHelper;

public class Const {
    public static final String RFC1123_DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss Z";
    public static final String RFC1123_SHORT_DATE_PATTERN = "EEE, dd MMM yyyy HH:mm";
    public static final String RSS_URL = "http://www.ted.com/themes/rss/id/6";
    public static final long UPDATE_TIMEOUT = 86400000;
    public static final String ARRTICLES_TABLE_PATH = "sqlite://ru.korsander.tedrss/" + TedRssDBHelper.TABLE_ARTICLES;
}

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.registryviewer.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class AppErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";
    private static final String DEFAULT_ERROR_VIEW = "error/error";

    private static final Logger logger = LoggerFactory.getLogger(AppErrorController.class);

    @RequestMapping(ERROR_PATH)
    public ModelAndView error(HttpServletRequest request) {
        logger.error("Error view was displayed. Path={}",
                (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI));

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(DEFAULT_ERROR_VIEW);
        modelAndView.addObject("hideMenu", true);

        return modelAndView;
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}

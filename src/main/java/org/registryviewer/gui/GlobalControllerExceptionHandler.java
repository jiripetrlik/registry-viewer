package org.registryviewer.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    private static final String DEFAULT_ERROR_VIEW = "error/error";

    private static final Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultExceptionHandler(HttpServletRequest req, Exception e) {
        logger.error("Error processing request {}", e);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(DEFAULT_ERROR_VIEW);
        modelAndView.addObject("hideMenu", true);

        return modelAndView;
    }
}

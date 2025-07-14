package com.bookStore.bookstore.configurations;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.ui.Model;

import java.util.Map;

@ControllerAdvice
@Component
public class GlobalModelAttributes {

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpSession session) {
        Object rawCart = session.getAttribute("cart");
        int cartSize = 0;

        if (rawCart instanceof Map<?, ?> map) {
            cartSize = map.values().stream()
                    .filter(Integer.class::isInstance)
                    .mapToInt(val -> (Integer) val)
                    .sum();
        }

        model.addAttribute("cartSize", cartSize);
    }
}

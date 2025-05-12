package ord.example.LegalManagementSystem.configuration;

import ord.example.LegalManagementSystem.util.SortUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("sortUtil")
    public SortUtils sortUtil() {
        return new SortUtils();
    }
}


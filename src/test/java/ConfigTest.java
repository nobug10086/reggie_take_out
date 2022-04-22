import com.itheima.reggie.entity.Employee;
import org.springframework.context.annotation.Bean;

/**
 * @author LJM
 * @create 2022/4/16
 */


public class ConfigTest {

    @Bean
    public Employee create(){
        Employee employee = new Employee();
        return employee;
    }

}

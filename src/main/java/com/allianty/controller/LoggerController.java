package com.allianty.controller;



import org.springframework.web.bind.annotation.RestController;


@RestController
public class LoggerController {
//    @Autowired
//    LoggerService loggerService;

//    @AopLogger
//    @RequestMapping("/findAll")
//    public Map<String,Object> findAll(){
//        List<LoggerDTO> loggerList = loggerService.findAll();
//        Map<String,Object> map=new HashMap<>();
//        map.put("loggerList",loggerList);
//        return map;
//    }
//
//    @RequestMapping("/hello")
//    @AopLogger
//    public LoggerDTO  selectById(Integer id){
//        LoggerDTO loggerDTO = loggerService.selectById(id);
//        return loggerDTO;
//    }

//    /**
//     * 将日志数据存入数据库
//     * @param loggerDTO
//     * @return
//     */
//    @RequestMapping("/doLogger")
//    public String  saveLogger(@RequestBody LoggerDTO loggerDTO){
//             loggerService.save(loggerDTO);
//        return "true";
//    }
//
//    @RequestMapping("/doCheck")
//    public String  checkStatus(){
//        String status = loggerService.checkStatus();
//        return status;
//    }
}

package com.softedge.solution.controller;

import com.opencsv.CSVWriter;
import com.softedge.solution.contractmodels.ClientCM;
import com.softedge.solution.service.CertusClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    CertusClientService certusClientService;


    @GetMapping("/doc-request/users")
    public ResponseEntity getClientRequestedUsers(HttpServletRequest request,
                                                  @RequestParam(value="search",required = false) String search,
                                                  @RequestParam(value="results",required = false) Integer results,
                                                  @RequestParam(value="page",required = false) Integer page,
                                                  @RequestParam(value="orderBy",required = false) String orderBy,
                                                  @RequestParam(value="sort",required = false) String sort) {

        if (results == null) {
            results = 10;
        }
        if (page == null || page == 0) {
            page = 0;
        } else if (page == 1) {
            page -= 1;
        } else {
            page = (page - 1) * results;
        }
        if (orderBy == null) {
            orderBy = "name";
        }
        if (sort == null) {
            sort = "asc";
        }
        return certusClientService.getClientRequestedUsers(request, search, page, results, orderBy, sort);

    }

    @GetMapping("/doc-request-count/users")
    public ResponseEntity getClientRequestedUserCount(HttpServletRequest request,
                                                      @RequestParam(value = "search", required = false) String search) {
        return certusClientService.getClientRequestedUsersCount(request, search);
    }


    @GetMapping("/user-info")
    public ResponseEntity getClientInfo(HttpServletRequest request) {
        return certusClientService.getClientInfo(request);
    }

    @PutMapping("/user-info")
    public ResponseEntity updateClientInfo(HttpServletRequest request, @RequestBody ClientCM clientCM) {
        return certusClientService.updateClientInfo(request, clientCM);
    }

    @PostMapping(value = "/generate-user-details-data-csv", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void exportSolutionSourceCSV(HttpServletResponse response,
                                        @RequestBody List<String> usernames) throws Exception {
        //set file name and content type
        String filename = "certus_user.csv";

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        /* CSV writer using comma as the seperator so the output data has modified in such a way that it will not
        contain any space to avoid miss interpretation of data by CSV */
        CSVWriter writer = new CSVWriter(
                response.getWriter(),
                ',',
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END
        );
        writer.writeAll(
                certusClientService.generateUserDataCSV(usernames));
        writer.close();
    }

    @GetMapping("/dashboard/user-summary")
    public ResponseEntity<?> getClientDashboardUserSummary(HttpServletRequest request) {
        return certusClientService.getClientDashboardUsersCount(request);
    }

}

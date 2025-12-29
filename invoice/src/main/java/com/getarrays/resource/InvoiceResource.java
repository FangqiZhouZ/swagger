package com.getarrays.resource;

import com.getarrays.constant.SwaggerConstant;
import com.getarrays.entity.Invoice;
import com.getarrays.service.InvoiceService;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping("/invoice")
@Api(tags = { SwaggerConstant.API_TAG })
public class InvoiceResource {
    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceResource(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @ApiOperation(value = "Add a new invoice", notes = "Add a new information into the system", response = Invoice.class)
    @ApiResponses({@ApiResponse(responseCode = "200", description = "The invoice saved Successfully"),
            @ApiResponse(responseCode = "500", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "400", description = "The request is malformed or invalid"),
            @ApiResponse(responseCode = "404", description = "The resource URL was not found on the server"),
            @ApiResponse(responseCode = "403", description = "You are not authorized. Please authenticate and try again"),
            @ApiResponse(responseCode = "401", description = "You don't have permission to this resource")
    })
    @PostMapping("/add")
    public ResponseEntity<Invoice> saveInvoice(@ApiParam(value = "Invoice to be saved", required = true) @RequestBody Invoice invoice) {
        Invoice newInvoice = invoiceService.saveInvoice(invoice);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/invoice/").toUriString()
                + newInvoice.getInvoiceNumber());
        return ResponseEntity.created(uri).body(newInvoice);
    }

    @ApiOperation(value = "Update an existing invoice", notes = "Update an invoice by passing in the invoice information with an existing invoice number", response = Invoice.class)
    @ApiResponses({@ApiResponse(responseCode = "200", description = "The invoice was updated successfully"),
            @ApiResponse(responseCode = "400", description = "The request is malformed or invalid"),
            @ApiResponse(responseCode = "404", description = "The resource URL was not found on the server"),
            @ApiResponse(responseCode = "500", description = "An internal server error occurred"),
            @ApiResponse(responseCode = "403", description = "You are not authorized. Please authenticate and try again"),
            @ApiResponse(responseCode = "401", description = "You don't have permission to this resource")
    })
    @PutMapping("/update")
    public ResponseEntity<Invoice> updateInvoice(@ApiParam(value = "invoice object in Json format")@RequestBody Invoice invoice) {
        Invoice updatedInvoice = invoiceService.updateInvoice(invoice);
        return ResponseEntity.ok().body(updatedInvoice);
    }

    @ApiOperation(value = "Get all available invoices", notes = "Retrieve a list of all invoices")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "The list of invoices retrieved"),
            @ApiResponse(responseCode = "400", description = "The request is malformed or invalid"),
            @ApiResponse(responseCode = "404", description = "The resource URL was not found on the server"),
            @ApiResponse(responseCode = "500", description = "An internal server error occurred"),
            @ApiResponse(responseCode = "403", description = "You are not authorized. Please authenticate and try again"),
            @ApiResponse(responseCode = "401", description = "You don't have permission to this resource")
    })
    @GetMapping("/all")
    public ResponseEntity<Collection<Invoice>> getInvoices() {
        Collection<Invoice> invoices = invoiceService.getInvoices();
        return ResponseEntity.ok().body(invoices);
    }

    @ApiOperation(value = "Get all invoices for a customer", notes = "Retrieve a list of invoices for a given customer")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "The invoice was deleted successfully"),
            @ApiResponse(responseCode = "400", description = "The request is malformed or invalid"),
            @ApiResponse(responseCode = "404", description = "The resource URL was not found on the server"),
            @ApiResponse(responseCode = "500", description = "An internal server error occurred"),
            @ApiResponse(responseCode = "403", description = "You are not authorized. Please authenticate and try again"),
            @ApiResponse(responseCode = "401", description = "You don't have permission to this resource")
    })
    @GetMapping("/customer/{customer}")
    public ResponseEntity<Collection<Invoice>> findInvoicesByCustomer(@ApiParam(value = "customer name")@PathVariable("customer")  String customer) {
        Collection<Invoice> invoices = invoiceService.findInvoicesByCustomer(customer);
        return ResponseEntity.ok().body(invoices);
    }

    @ApiOperation(value = "Find an invoice by its number", notes = "Retrieve an invoice by passing the invoice number", response = Invoice.class)
    @ApiResponses({@ApiResponse(responseCode = "200", description = "The invoice for that number"),
            @ApiResponse(responseCode = "400", description = "The request is malformed or invalid"),
            @ApiResponse(responseCode = "404", description = "The resource URL was not found on the server"),
            @ApiResponse(responseCode = "500", description = "An internal server error occurred"),
            @ApiResponse(responseCode = "403", description = "You are not authorized. Please authenticate and try again"),
            @ApiResponse(responseCode = "401", description = "You don't have permission to this resource")
    })
    @GetMapping("/{invoiceNumber}")
    public ResponseEntity<Invoice> findInvoice(@ApiParam(value = "invoice number")@PathVariable("invoiceNumber")  String invoiceNumber) {
        Invoice invoice = invoiceService.findInvoice(invoiceNumber);
        return ResponseEntity.ok().body(invoice);
    }

    @ApiOperation(value = "Delete an invoice by its number", notes = "Delete an invoice by passing in the invoice number")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "The invoice was deleted successfully"),
            @ApiResponse(responseCode = "400", description = "The request is malformed or invalid"),
            @ApiResponse(responseCode = "404", description = "The resource URL was not found on the server"),
            @ApiResponse(responseCode = "500", description = "An internal server error occurred"),
            @ApiResponse(responseCode = "403", description = "You are not authorized. Please authenticate and try again"),
            @ApiResponse(responseCode = "401", description = "You don't have permission to this resource")
    })
    @DeleteMapping("/delete/{invoiceNumber}")
    public ResponseEntity<?> deleteInvoice(@ApiParam(value = "invoice number")@PathVariable("invoiceNumber")  String invoiceNumber) {
        invoiceService.deleteInvoice(invoiceNumber);
        return ResponseEntity.ok().build();
    }
}

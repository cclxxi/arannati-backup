package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.ProductDTO;
import kz.arannati.arannati.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface CatalogExportService {
    ByteArrayOutputStream exportToPdf(List<ProductDTO> products, String title, UserDTO user);
    ByteArrayOutputStream exportToExcel(List<ProductDTO> products, String title);
}

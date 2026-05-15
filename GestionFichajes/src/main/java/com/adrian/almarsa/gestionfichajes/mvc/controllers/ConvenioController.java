package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.*;

@Controller
@RequestMapping("/convenio")
public class ConvenioController {

    // Ruta donde se guardará el archivo en el servidor
    private final Path rootPath = Paths.get("uploads/documentos");
    private final String FILE_NAME = "convenio_colectivo.pdf";

    @GetMapping
    public String verConvenio(HttpSession session, Model model) {
        if (session.getAttribute("rol") == null) return "redirect:/login";
        
        // Verificamos si el archivo existe para mostrar el visor o un mensaje
        boolean existe = Files.exists(rootPath.resolve(FILE_NAME));
        model.addAttribute("existeArchivo", existe);
        model.addAttribute("usuario", session.getAttribute("usuario")); // Si guardaste el objeto en sesión
        model.addAttribute("rol", session.getAttribute("rol"));
        
        return "convenio"; 
    }

    @PostMapping("/subir")
    public String subirArchivo(@RequestParam("archivo") MultipartFile archivo, 
                               HttpSession session, RedirectAttributes flash) {
        
        // Seguridad: Solo ADMIN puede subir
        if (!"ADMIN".equals(session.getAttribute("rol"))) {
            return "redirect:/convenio";
        }

        if (archivo.isEmpty() || !archivo.getContentType().equals("application/pdf")) {
            flash.addFlashAttribute("mensajeError", "Error: El archivo debe ser un PDF válido.");
            return "redirect:/convenio";
        }

        try {
            if (!Files.exists(rootPath)) Files.createDirectories(rootPath);
            
            Files.copy(archivo.getInputStream(), 
                       rootPath.resolve(FILE_NAME), 
                       StandardCopyOption.REPLACE_EXISTING);
            
            flash.addFlashAttribute("mensajeExito", "Convenio actualizado correctamente.");
        } catch (IOException e) {
            flash.addFlashAttribute("mensajeError", "Error al guardar el archivo en el servidor.");
        }

        return "redirect:/convenio";
    }

    @GetMapping("/descargar")
    @ResponseBody
    public ResponseEntity<Resource> descargarPdf(HttpSession session) {
        if (session.getAttribute("rol") == null) return ResponseEntity.status(403).build();

        try {
            Path file = rootPath.resolve(FILE_NAME);
            Resource recurso = new UrlResource(file.toUri());

            if (recurso.exists() || recurso.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + FILE_NAME + "\"")
                        .body(recurso);
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.notFound().build();
    }
}
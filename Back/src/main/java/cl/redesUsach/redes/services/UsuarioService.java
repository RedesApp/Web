package cl.redesUsach.redes.services;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import cl.redesUsach.redes.models.Usuario;
import cl.redesUsach.redes.repositories.UsuarioRepository;

@CrossOrigin
@RestController
@RequestMapping("/usuarios")
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Transactional
	@PostMapping
	public Usuario saveUsuario(@RequestBody Usuario usuario) {
		
		return usuarioRepository.save(usuario);
		
	}
	
	@Transactional
	@GetMapping("{id}")
	public Usuario findUsuarioByid(@PathVariable("id")Long id) {
		
		return usuarioRepository.findById(id).orElse(null);
	}
	
	@Transactional
	@DeleteMapping
	public HttpStatus deleteUsuario(@PathVariable("id")Long id) {
		
		usuarioRepository.deleteById(id);
		return HttpStatus.OK;
	}
	
	@Transactional
	@PutMapping("{id}")
	public HttpStatus updateUsuario(@PathVariable("id")Long id,@RequestBody Usuario usuario) {
		
		Usuario currentUsuario=usuarioRepository.findById(id).orElse(null);
		
		if(currentUsuario==null)return HttpStatus.NOT_ACCEPTABLE;
		
		currentUsuario.setApellido(usuario.getApellido());
		currentUsuario.setContraseña(usuario.getContraseña());
		currentUsuario.setNombre(usuario.getNombre());
		currentUsuario.setEmail(usuario.getEmail());
		
		return HttpStatus.OK;
		
	}
	
	
	@PostMapping("auth")
	@Transactional
	public Usuario auth(@RequestBody String json) throws JSONException {
		
		JSONObject response = new JSONObject(json);
		String email= response.getString("email");
		String contraseña=response.getString("contraseña");
		
		
		Usuario usuario=usuarioRepository.findByEmail(email).orElse(null);
		
		if(usuario==null || !usuario.getContraseña().equals(contraseña)) {
			return null;
		}
		
		return usuario;
	}

	@GetMapping("/send")
	@Transactional
	public HttpStatus sendEmail() throws FileNotFoundException, DocumentException {
		File archivo= new File("iTextHelloWorld.pdf");
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(archivo));
		 
		document.open();
		Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK);
		Chunk chunk = new Chunk("Hello World", font);
		document.add(chunk);
		document.close();
		
		try {
			// Propiedades de la conexión
			

			String to = "cuentatest111@gmail.com";
			String from = "cuentatest111@gmail.com";
			String pass = "cuentatest11123";
			Properties props = new Properties();
			props.setProperty("mail.smtp.host", "smtp.gmail.com");
			props.setProperty("mail.smtp.starttls.enable", "true");
			props.setProperty("mail.smtp.port", "587");
			props.setProperty("mail.smtp.user", from);
			props.setProperty("mail.smtp.auth", "true");

			// Preparamos la sesion
			Session session = Session.getDefaultInstance(props);
			
			BodyPart texto = new MimeBodyPart();
            texto.setText("Texto del mensaje");

            // Se compone el adjunto con la imagen
            BodyPart adjunto = new MimeBodyPart();
            adjunto.setDataHandler(
                new DataHandler(new FileDataSource("iTextHelloWorld.pdf")));
            adjunto.setFileName("archivo.pdf");
			
            
         // Una MultiParte para agrupar texto e imagen.
            MimeMultipart multiParte = new MimeMultipart();
            multiParte.addBodyPart(texto);
            multiParte.addBodyPart(adjunto);

//			// Construimos el mensaje
//			MimeMessage message = new MimeMessage(session);
//			message.setFrom(new InternetAddress(from));
//			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//			message.setSubject("Subject");
//			message.setText("Mensaje");
            
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
            message.setSubject("Hola");
            message.setContent(multiParte);

			// Lo enviamos.
			Transport t = session.getTransport("smtp");
			t.connect(from, pass);
			t.sendMessage(message, message.getAllRecipients());

			// Cierre.
			t.close();
			archivo.delete();
			return HttpStatus.OK;

		} catch (Exception e) {
			archivo.delete();
			e.printStackTrace();
			return HttpStatus.BAD_GATEWAY;
		}
	}
	
	

}

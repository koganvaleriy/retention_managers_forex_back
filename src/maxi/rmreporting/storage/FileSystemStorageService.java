package maxi.rmreporting.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import static maxi.rmreporting.api.russian.FileNamesAndColumnTitles.*;
import static maxi.rmreporting.api.international.FileNamesAndColumnTitlesAr.*;
import static maxi.rmreporting.api.international.FileNamesAndColumnTitlesEn.*;
import static maxi.rmreporting.api.international.FileNamesAndColumnTitlesSp.*;

@Service
public class FileSystemStorageService implements StorageService {

	private final Path rootLocation;

	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Override
	public void store(MultipartFile file) {
		
		File uploadDir = rootLocation.toFile();
		if (!uploadDir.exists())
			uploadDir.mkdir();
		
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + filename);
			}
			if (filename.contains("..")) {
				// This is a security check
				throw new StorageException(
						"Cannot store file with relative path outside current directory " + filename);
			}
					
			Files.copy(file.getInputStream(), this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new StorageException("Failed to store file " + filename, e);
		}
	}
/*
	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
					.map(path -> this.rootLocation.relativize(path));
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new StorageFileNotFoundException("Could not read file: " + filename);

			}
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}
*/
	@Override
	public boolean deleteAll() {
		try {
			FileSystemUtils.deleteRecursively(rootLocation.toFile());
			File uploadDir = rootLocation.toFile();
			uploadDir.mkdir();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public String checkFilesExisting() {

		StringBuilder res = new StringBuilder();
		
		try {
			File[] files= new File[16];
			files[0] = new File(ACTIVATIONS_FILE);
			files[1] = new File(LOCKS_FILE);
			files[2] = new File(MT4_OPENED_BY_DEALER_FILE);
			files[3] = new File(BALANCES_FILE);
			files[4] = new File(BALANCES_LAST_MONTH_FILE);
			files[5] = new File(CLOSED_DEALS_FILE);
			files[6] = new File(CREDITS_FILE);
			files[7] = new File(DEPOSITSFILE);
			files[8] = new File(NB_OPENED_BY_DEALER_FILE);
			files[9] = new File(WITHDRAWALS_FILE);
			files[10] = new File(REOPENINGS_FILE);
			files[11] = new File(RETMANAGERSFILE);
			files[12] = new File(LAST_MONTH_REPORT);
			files[13] = new File(LAST_MONTH_REPORT_AR);
			files[14] = new File(LAST_MONTH_REPORT_EN);
			files[15] = new File(LAST_MONTH_REPORT_SP);
			
			for (int i = 0; i < files.length; i++) {
				if(!files[i].exists()) {
					res.append("\n" + files[i].getPath().substring(11) + ",");
				}
			}
			
			if(res.length()==0){
				res.append("OK");
			}
			        
			return res.toString();
		} catch (Exception e) {
			return res.toString();
		}
	}
/*
	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
	*/
	/*
	@Override
	public List<File> readFileResults() {
		File[] resFilesExp= new File[4];
		List<File> resFilesActual = new ArrayList<>();
		
		resFilesExp[0] = new File(OUTPUT_FILENAME);
		resFilesExp[1] = new File(OUTPUT_FILENAME_AR);
		resFilesExp[2] = new File(OUTPUT_FILENAME_EN);
		resFilesExp[3] = new File(OUTPUT_FILENAME_SP);
		
		for (int i = 0; i < resFilesExp.length; i++) {
			if(resFilesExp[i].exists()) {
				resFilesActual.add(resFilesExp[i]);
			}
		}
		for (File file: resFilesActual) {
				System.out.println(file);
		}
		return resFilesActual;

	}
	*/
}

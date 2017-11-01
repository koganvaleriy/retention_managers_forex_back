package maxi.rmreporting.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

	//void init();
	void store(MultipartFile file);
	//Stream<Path> loadAll();
	//Path load(String filename);
	//Resource loadAsResource(String filename);
	boolean deleteAll();
	String checkFilesExisting();


}

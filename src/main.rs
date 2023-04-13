extern crate fs_extra;
extern crate dirs;
use std::{fs, path::Path};
use fs_extra::{dir::CopyOptions, TransitProcess};

fn main() {
    // Default destination for modified discord cache files
    let destination = String::from("./discord_cache");

    // Create discord_cache directory, and remove it and its contents if exists
    if Path::new(&destination).exists() {
        fs::remove_dir_all(&destination).unwrap();
    }
    fs::create_dir(&destination).unwrap();
    
    let options = CopyOptions::new(); //Initialize default values for CopyOptions
    let handle = |process_info: TransitProcess| {
       println!("Copying files to ./discord_cache directory: {}", process_info.file_name);
       fs_extra::dir::TransitProcessResult::ContinueOrAbort
    };


    let path_to_discord_cache = String::from(dirs::home_dir().unwrap().to_str().unwrap().to_owned() + "\\AppData\\Roaming\\discord\\Cache\\Cache_Data");

    let mut from_paths = Vec::new();
    for file in fs::read_dir(path_to_discord_cache).unwrap() {
        let path = file.unwrap().path().to_str().unwrap().to_owned();
        from_paths.push(path);
    }

    fs_extra::copy_items_with_progress(&from_paths, &destination, &options, handle).unwrap();

    // Change file extensions to correct mime types
    for file in fs::read_dir(&destination).unwrap() {
        let entry = file.unwrap();
        let path_buffer = entry.path();
        let path = path_buffer.to_str().unwrap();

        let kind = infer::get_from_path(path);

        let kind = match kind {
            Ok(type_option) => type_option,
            Err(_error) => continue,
        };

        match kind {
            Some(value) => {
                let mime = value.mime_type().to_string();
                let mime_array: Vec<&str> = mime.split("/").collect();

                let together = format!("{}.{}", path, mime_array[1]);

                fs::rename(path, together).unwrap();
                println!("Adding ext to {}", entry.file_name().to_str().unwrap())
            },
            None => println!("Ignoring file: {} - No MIME type", entry.file_name().to_str().unwrap()),
        }
    }
}
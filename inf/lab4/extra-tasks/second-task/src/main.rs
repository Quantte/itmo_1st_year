use std::{env, fs};
use serde_json::Value;

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let path = env::args()
        .nth(1)
        .unwrap_or_else(|| "schedule.hcl".to_string());

    let input = fs::read_to_string(&path)?; 
    let value: Value = hcl::from_str(&input)?;
    let yaml = serde_yaml::to_string(&value)?;
    
    match fs::write("schedule.yaml", yaml) {
        Ok(()) => println!("File \"schedule.yaml\" saved"),
        Err(e) => panic!("Error saving file: {}", e)    
    }
    Ok(())
}

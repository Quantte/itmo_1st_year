use std::io::{self, Write};

fn get_input() -> Result<Vec<bool>, String> {
    print!("Enter the sequence of 7 bit [0, 1]: ");
    let _ = io::stdout().flush();
    let mut input = String::new();
    io::stdin().read_line(&mut input).expect("failed to read line");
    input = input.trim().to_string();

    if input.len() < 7 {
        return Err("Not enough bit".to_string());
    } else if input.len() > 7 {
        return Err("Too much bits".to_string());
    }

    let mut data: Vec<bool> = Vec::new();
    for i in input.chars() {
        match i {
            '1' => data.push(true),
            '0' => data.push(false),
            _ => { return Err("Wrong symbol was found".to_string()) }
        }
    }
    return Ok(data);
}

fn analyze(data: Vec<bool>) ->  (Vec<bool>, Result<(), u8>) {
    let s1: bool = data[0] ^ data[2] ^ data[4] ^ data[6];
    let s2: bool = data[1] ^ data[2] ^ data[5] ^ data[6];
    let s3: bool = data[3] ^ data[4] ^ data[5] ^ data[6];

    let s = ((s3 as u8) << 2) + ((s2 as u8) << 1) + (s1 as u8);
    
    match s {
        0 => { (vec![data[2], data[4], data[5], data[6]], Ok(())) },
        _ => { 
            let mut cdata = data;
            cdata[(s as usize) - 1] = !cdata[(s as usize)-1];
            (vec![cdata[2], cdata[4], cdata[5], cdata[6]], Err(s))
        }
    }
}

fn form_respose(data: (Vec<bool>, Result<(), u8>)) {
    let (symb, res) = data;
    let mut out = String::new();
    for d in symb {
        match d {
            true => out.push('1'),
            false => out.push('0'),
        }
    }

    match res {
        Ok(()) => {
            println!("data bits: {}", out);
            println!();
        },
        Err(err) => {
            println!("data bits: {}", out);
            println!("Error was found in {}'s bit", err)
        } 
    }
}


fn main() {
    loop {
        match get_input() {
            Ok(data) => form_respose(analyze(data)),
            Err(err) => println!("{err}")
        }
        println!();
    }
}

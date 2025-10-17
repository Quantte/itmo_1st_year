use std::io::{self, Write};

fn get_input() -> String {
    print!("Enter your numder: ");
    io::stdout().flush().unwrap();

    let mut user_input = String::new();
    io::stdin().read_line(&mut user_input)
        .expect("Failed to read line");

    user_input.trim().replace(",", ".").to_string()
}


fn calc_reminders(input: String) -> Result<Vec<i8>, String> {

    let mut remainders: Vec<i8> = Vec::new();
    let mut remainder: i8;

    if let Ok(i) = input.parse::<i64>() {
        let mut num: i64 = i;
        if num == 0 {
            remainders.push(0);
            return Ok(remainders);
        } 
        while num != 0 {
            remainder = (num % 7) as i8;
            num /= 7;
            if remainder > 3 {
               remainder -= 7;
                num += 1;
            } else if remainder < -3 {
                remainder += 7;
                num -= 1;
            }
            remainders.push(remainder);
        }
        remainders.reverse();    
        return Ok(remainders);

    } else if let Ok(f) = input.parse::<f64>() {

        let num: f64 = f;
        if num >= 9223372036854776000.0 || 
             num <= -9223372036854776000.0 { 
            return Err("Value is too large".to_string()); 

        }        
        if num == 0.0 {
            return Ok(vec![0]);
        }
        
        let mut integer: i64 = num as i64;
        let mut mantissa = num - integer as f64;
        if integer >= 100000000000000 || 
           integer <= -100000000000000 { 
            return Err("Value is too large".to_string()); 
        }
        if integer == 0 {
            remainders.push(0);
        }
        while integer != 0 {
            remainder = (integer % 7) as i8;
            integer /= 7;
            if remainder > 3 {
                remainder -= 7;
                integer += 1;
            } else if remainder < -3 {
                remainder += 7;
                integer -= 1;
            }
            remainders.push(remainder);
        }
        remainders.reverse();
        remainders.push(-128);

        for _ in 0..10 {
            if mantissa == 0.0 {
                remainders.push(0);
                break;
            }
            mantissa *= 7.0;
            remainder = mantissa as i8;

            if remainder > 3 {
                remainder -= 7;
            } else if remainder < -3 {
                remainder += 7;
            }
            remainders.push(remainder);
            mantissa -= (mantissa as i64) as f64;
        }
        return Ok(remainders);
    } else {
        return Err("Not A Num".to_string());
    }
}


fn build_output(remainders: Vec<i8>) -> String {
    let mut out = String::new();

    for r in remainders {
        match r {
            3 => out.push('3'),
            2 => out.push('2'),
            1 => out.push('1'),
            0 => out.push('0'),
            -1 => out.push_str("{^1}"),
            -2 => out.push_str("{^2}"),
            -3 => out.push_str("{^3}"),
            -128 => out.push('.'),
            _ => panic!("Error building answer")
        }
    }
    return out;
}


fn main() {
    loop {
        let input = get_input(); 
        // let rems = calc_reminders(input);
        let rems = match calc_reminders(input) {
            Ok(n) => n,
            Err(error) => {
                println!("Error: {}\n", error);
                continue;
            }
        };
        let out = build_output(rems);
        println!("The answer is: {}\n", out);
    }
}

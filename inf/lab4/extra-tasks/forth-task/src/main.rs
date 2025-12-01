use std::process::{Command, Stdio};
use std::time::Instant;


fn run_base_task() {
    let _ = Command::new("../../main-task/hcl_parser")
        .arg("schedule.hcl")
        .stdout(Stdio::null())
        .status()
        .expect("failed to run executable");
}

fn run_first_task() {
    let _ = Command::new("../first-task/target/release/first-task")
        .arg("schedule.hcl")
        .stdout(Stdio::null())
        .status()
        .expect("failed to run executable");
}

fn run_second_task() {
    let _ = Command::new("../second-task/target/release/second-task")
        .arg("schedule.hcl")
        .stdout(Stdio::null())
        .status()
        .expect("failed to run executable");
}

fn run_third_task() {
    let _ = Command::new("../third-task/target/release/third-task")
        .arg("schedule.hcl")
        .stdout(Stdio::null())
        .status()
        .expect("failed to run executable");
}


fn main() {
    const ITER_NUM: u32 = 100;
    
    let start = Instant::now();
    for _ in 0..ITER_NUM {
         run_base_task();   
    }
    let elapsed = start.elapsed();
    println!("Десериализация flex & bison");
    println!("Общее время для {} запусков: {:.3?}", ITER_NUM, elapsed);
    println!("Среднее на один запуск: {:.3?}\n", elapsed / ITER_NUM);


    let start = Instant::now();
    for _ in 0..ITER_NUM {
         run_first_task();   
    }
    let elapsed = start.elapsed();
    println!("Десериализация flex & bison + конвертация в YAML");
    println!("Общее время для {} запусков: {:.3?}", ITER_NUM, elapsed);
    println!("Среднее на один запуск: {:.3?}\n", elapsed / ITER_NUM);


    let start = Instant::now();
    for _ in 0..ITER_NUM {
         run_second_task();   
    }
    let elapsed = start.elapsed();
    println!("Конвертация в YAML с кастомными библиотеками");
    println!("Общее время для {} запусков: {:.3?}", ITER_NUM, elapsed);
    println!("Среднее на один запуск: {:.3?}\n", elapsed / ITER_NUM);


    let start = Instant::now();
    for _ in 0..ITER_NUM {
         run_third_task();   
    }
    let elapsed = start.elapsed();
    println!("Конвертация в XML");
    println!("Общее время для {} запусков: {:.3?}", ITER_NUM, elapsed);
    println!("Среднее на один запуск: {:.3?}\n", elapsed / ITER_NUM);
}

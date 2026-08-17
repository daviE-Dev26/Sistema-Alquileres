import { ComponentFixture,TestBed } from '@angular/core/testing';
import { RegistroPropietario } from './registro-propietario';

describe('RegistroPropietario',()=>{

let component:RegistroPropietario;
let fixture:ComponentFixture<RegistroPropietario>;

beforeEach(async()=>{

await TestBed.configureTestingModule({
imports:[RegistroPropietario],
}).compileComponents();

fixture=TestBed.createComponent(RegistroPropietario);

component=fixture.componentInstance;

await fixture.whenStable();

});

it('should create',()=>{

expect(component).toBeTruthy();

});

});
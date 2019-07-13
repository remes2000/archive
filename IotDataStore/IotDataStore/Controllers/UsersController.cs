using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using IotDataStore.Models;
using IotDataStore.Models.RequestBodies;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.IdentityModel.Tokens;
using Microsoft.Extensions.Configuration;
using Microsoft.AspNetCore.Authorization;

namespace IotDataStore.Controllers
{
    [Produces("application/json")]
    [Route("api/Users")]
    public class UsersController : Controller
    {
        private readonly IotDataStoreContext _context;
        private readonly IConfiguration _config;

        public UsersController(IotDataStoreContext context, IConfiguration config)
        {
            _context = context;
            _config = config;
        }

        // POST: api/Users
        [HttpPost]
        public IActionResult PostUser([FromBody] NewUser user)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(user);
            }

            if (_context.Users.Any(u => u.Username == user.Username))
                return Ok(new { error = "This username is already taken" });

            if (_context.Users.Any(u => u.Email == user.Email))
                return Ok(new { error = "This email is already taken" });

            User userToAdd = new User()
            {
                Username = user.Username,
                Email = user.Email,
                PasswordHash = BCrypt.Net.BCrypt.HashPassword(user.Password)
            };

            _context.Users.Add(userToAdd);
            _context.SaveChanges();

            return Ok(new { message = "Successful registered"});
        }

        //POST: api/Users/authenticate
        [AllowAnonymous]
        [HttpPost]
        [Route("/api/Users/authenticate")]
        public IActionResult Authenticate([FromBody] LoginCredentials loginData)
        {
            if(!ModelState.IsValid)
            {
                return BadRequest();
            }

            User user = _context.Users.SingleOrDefault(u => u.Username == loginData.username && BCrypt.Net.BCrypt.Verify(loginData.password, u.PasswordHash));
            if (user != null)
            {
                var tokenString = BuildToken(user);
                return Ok(new {
                    Id = user.Id,
                    Username = user.Username,
                    Email = user.Email,
                    Token = tokenString
                });
            }
            else
                return Unauthorized();
        }

        private string BuildToken(User user)
        {
            var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_config["Jwt:Key"]));
            var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            var claims = new[]
            {
                new Claim(JwtRegisteredClaimNames.UniqueName, user.Username),
                new Claim(JwtRegisteredClaimNames.Email, user.Email),
                new Claim(JwtRegisteredClaimNames.Jti, user.Id.ToString()),
            };

            var token = new JwtSecurityToken(_config["Jwt:Issuer"],
                _config["Jwt:Issuer"],
                claims,
                expires: DateTime.Now.AddDays(30),
                signingCredentials: creds);

            return new JwtSecurityTokenHandler().WriteToken(token);
        }
    }
}